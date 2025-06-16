package com.example.appcomunidades.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcomunidades.repositorios.AuthRepositorio
import com.example.appcomunidades.componentes.DatosPerfilUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Estados para el perfil del usuario
 */
sealed class EstadoPerfil {
    object Inicial : EstadoPerfil()
    object Cargando : EstadoPerfil()
    data class Exito(val datos: DatosPerfilUsuario) : EstadoPerfil()
    data class Error(val mensaje: String) : EstadoPerfil()
}

/**
 * Estados para cerrar sesión
 */
sealed class EstadoCerrarSesion {
    object Inicial : EstadoCerrarSesion()
    object Cargando : EstadoCerrarSesion()
    object Completado : EstadoCerrarSesion()
    data class Error(val mensaje: String) : EstadoCerrarSesion()
}

/**
 * ViewModel para manejar el perfil del usuario y operaciones relacionadas
 */
class PerfilViewModel : ViewModel() {

    private val authRepositorio = AuthRepositorio()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Estados del perfil
    private val _estadoPerfil = MutableStateFlow<EstadoPerfil>(EstadoPerfil.Inicial)
    val estadoPerfil: StateFlow<EstadoPerfil> = _estadoPerfil.asStateFlow()

    private val _datosPerfil = MutableStateFlow<DatosPerfilUsuario?>(null)
    val datosPerfil: StateFlow<DatosPerfilUsuario?> = _datosPerfil.asStateFlow()

    // Estados de cerrar sesión
    private val _estadoCerrarSesion = MutableStateFlow<EstadoCerrarSesion>(EstadoCerrarSesion.Inicial)
    val estadoCerrarSesion: StateFlow<EstadoCerrarSesion> = _estadoCerrarSesion.asStateFlow()

    init {
        cargarDatosUsuario()
    }

    /**
     * Carga los datos del usuario autenticado desde Firebase
     */
    fun cargarDatosUsuario() {
        if (_estadoPerfil.value is EstadoPerfil.Cargando) return

        _estadoPerfil.value = EstadoPerfil.Cargando

        viewModelScope.launch {
            try {
                println("DEBUG: === CARGANDO DATOS DE PERFIL ===")

                val usuarioAuth = auth.currentUser
                if (usuarioAuth == null) {
                    println("DEBUG: ❌ No hay usuario autenticado")
                    _estadoPerfil.value = EstadoPerfil.Error("Usuario no autenticado")
                    return@launch
                }

                val email = usuarioAuth.email ?: ""
                val uid = usuarioAuth.uid
                val nombreAuth = usuarioAuth.displayName ?: ""

                println("DEBUG: Usuario Auth - UID: $uid, Email: $email")

                // Buscar datos completos en Firestore
                val datosFirestore = obtenerDatosDeFirestore(email.lowercase().trim())

                if (datosFirestore != null) {
                    println("DEBUG: ✅ Datos obtenidos de Firestore")
                    _datosPerfil.value = datosFirestore
                    _estadoPerfil.value = EstadoPerfil.Exito(datosFirestore)
                } else {
                    // Fallback: usar datos básicos de Authentication
                    println("DEBUG: ⚠️ Usando datos básicos de Auth")
                    val datosFallback = DatosPerfilUsuario(
                        nombre = nombreAuth.ifEmpty { "Usuario" },
                        email = email,
                        comunidadId = "No asignada",
                        esAdmin = false
                    )
                    _datosPerfil.value = datosFallback
                    _estadoPerfil.value = EstadoPerfil.Exito(datosFallback)
                }

            } catch (e: Exception) {
                println("DEBUG: ❌ Error cargando perfil: ${e.message}")
                e.printStackTrace()
                _estadoPerfil.value = EstadoPerfil.Error("Error al cargar el perfil: ${e.message}")
            }
        }
    }

    /**
     * Obtiene los datos del usuario desde Firestore
     */
    private suspend fun obtenerDatosDeFirestore(email: String): DatosPerfilUsuario? {
        return try {
            println("DEBUG: Buscando usuario en Firestore con email: $email")

            // Buscar por email normalizado
            val emailNormalizado = email.lowercase().trim()

            val snapshot = firestore.collection("usuarios")
                .whereEqualTo("email", emailNormalizado)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val documento = snapshot.documents[0]
                val datos = DatosPerfilUsuario(
                    nombre = documento.getString("nombre") ?: "Usuario",
                    email = documento.getString("email") ?: email,
                    comunidadId = documento.getString("comunidad_id") ?: "Sin asignar",
                    esAdmin = documento.getBoolean("es_admin") ?: false
                )

                println("DEBUG: ✅ Datos encontrados por email - Nombre: ${datos.nombre}, Admin: ${datos.esAdmin}")
                return datos
            }

            println("DEBUG: No encontrado por email, intentando por UID...")

            // Buscar por UID del usuario autenticado
            val uid = auth.currentUser?.uid ?: return null

            // Buscar documento que tenga referencia al UID o donde el ID del documento sea el UID
            val snapshotPorUid = firestore.collection("usuarios")
                .whereEqualTo("firebase_uid", uid)
                .get()
                .await()

            if (!snapshotPorUid.isEmpty) {
                val documento = snapshotPorUid.documents[0]
                val datos = DatosPerfilUsuario(
                    nombre = documento.getString("nombre") ?: "Usuario",
                    email = documento.getString("email") ?: email,
                    comunidadId = documento.getString("comunidad_id") ?: "Sin asignar",
                    esAdmin = documento.getBoolean("es_admin") ?: false
                )
                println("DEBUG: ✅ Datos encontrados por UID - Nombre: ${datos.nombre}")
                return datos
            }

            println("DEBUG: Intentando buscar por coincidencia parcial de email...")

            // Obtener TODOS los usuarios y buscar coincidencia
            val todosUsuarios = firestore.collection("usuarios")
                .get()
                .await()

            for (doc in todosUsuarios.documents) {
                val emailDoc = doc.getString("email")?.lowercase()?.trim()
                if (emailDoc == emailNormalizado) {
                    val datos = DatosPerfilUsuario(
                        nombre = doc.getString("nombre") ?: "Usuario",
                        email = doc.getString("email") ?: email,
                        comunidadId = doc.getString("comunidad_id") ?: "Sin asignar",
                        esAdmin = doc.getBoolean("es_admin") ?: false
                    )
                    println("DEBUG: ✅ Datos encontrados por búsqueda manual - Nombre: ${datos.nombre}")
                    return datos
                }
            }

            println("DEBUG: ❌ Usuario no encontrado con ninguna estrategia")
            null

        } catch (e: Exception) {
            println("DEBUG: ❌ Error consultando Firestore: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Cierra la sesión del usuario
     */
    fun cerrarSesion() {
        if (_estadoCerrarSesion.value is EstadoCerrarSesion.Cargando) return

        _estadoCerrarSesion.value = EstadoCerrarSesion.Cargando

        viewModelScope.launch {
            try {
                println("DEBUG: === CERRANDO SESIÓN ===")

                // Cerrar sesión en Firebase
                authRepositorio.cerrarSesion()

                // Limpiar datos locales
                _datosPerfil.value = null
                _estadoPerfil.value = EstadoPerfil.Inicial

                println("DEBUG: ✅ Sesión cerrada exitosamente")
                _estadoCerrarSesion.value = EstadoCerrarSesion.Completado

            } catch (e: Exception) {
                println("DEBUG: ❌ Error cerrando sesión: ${e.message}")
                _estadoCerrarSesion.value = EstadoCerrarSesion.Error("Error al cerrar sesión")
            }
        }
    }

    /**
     * Reinicia el estado de cerrar sesión
     */
    fun reiniciarEstadoCerrarSesion() {
        _estadoCerrarSesion.value = EstadoCerrarSesion.Inicial
    }

    /**
     * Fuerza la recarga de datos del perfil
     */
    fun refrescarPerfil() {
        cargarDatosUsuario()
    }

    /**
     * Obtiene datos básicos rápidos (para mostrar avatar mientras carga)
     */
    fun obtenerDatosBasicos(): DatosPerfilUsuario? {
        val usuarioAuth = auth.currentUser
        return if (usuarioAuth != null) {
            DatosPerfilUsuario(
                nombre = usuarioAuth.displayName ?: "Usuario",
                email = usuarioAuth.email ?: "",
                comunidadId = "Cargando...",
                esAdmin = false
            )
        } else {
            null
        }
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    fun hayUsuarioAutenticado(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Obtiene el UID del usuario actual
     */
    fun obtenerUidUsuario(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Obtiene el email del usuario actual
     */
    fun obtenerEmailUsuario(): String? {
        return auth.currentUser?.email
    }

    /**
     * Función para debug - mostrar información del usuario
     */
    fun debug_mostrarInfoUsuario() {
        val usuario = auth.currentUser
        if (usuario != null) {
            println("DEBUG: === INFO USUARIO ACTUAL ===")
            println("DEBUG: UID: ${usuario.uid}")
            println("DEBUG: Email: ${usuario.email}")
            println("DEBUG: Nombre: ${usuario.displayName}")
            println("DEBUG: Verificado: ${usuario.isEmailVerified}")
            println("DEBUG: =============================")
        } else {
            println("DEBUG: No hay usuario autenticado")
        }
    }

    /**
     * FUNCIÓN DE DEBUG TEMPORAL - Verificar datos en Firestore
     */
    fun debug_verificarFirestore() {
        val email = auth.currentUser?.email ?: return

        viewModelScope.launch {
            try {
                println("DEBUG: === VERIFICANDO FIRESTORE ===")
                println("DEBUG: Buscando usuario con email: $email")

                val snapshot = firestore.collection("usuarios")
                    .whereEqualTo("email", email)
                    .get()
                    .await()

                if (snapshot.isEmpty) {
                    println("DEBUG: ❌ PROBLEMA: Usuario NO encontrado en Firestore")
                    println("DEBUG: Email buscado: $email")

                    val todosUsuarios = firestore.collection("usuarios")
                        .get()
                        .await()

                    println("DEBUG: === USUARIOS EN FIRESTORE ===")
                    for (doc in todosUsuarios.documents) {
                        println("DEBUG: Email en DB: ${doc.getString("email")}")
                        println("DEBUG: Nombre en DB: ${doc.getString("nombre")}")
                        println("DEBUG: ID: ${doc.id}")
                        println("DEBUG: ---")
                    }
                } else {
                    println("DEBUG: ✅ Usuario encontrado en Firestore")
                    val doc = snapshot.documents[0]
                    println("DEBUG: Nombre: ${doc.getString("nombre")}")
                    println("DEBUG: Email: ${doc.getString("email")}")
                    println("DEBUG: Comunidad: ${doc.getString("comunidad_id")}")
                    println("DEBUG: Es Admin: ${doc.getBoolean("es_admin")}")
                }

            } catch (e: Exception) {
                println("DEBUG: ❌ Error verificando Firestore: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}