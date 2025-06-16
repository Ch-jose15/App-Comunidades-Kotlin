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
                val datosFirestore = obtenerDatosDeFirestore(email)

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

            val snapshot = firestore.collection("usuarios")
                .whereEqualTo("email", email)
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

                println("DEBUG: Datos encontrados - Nombre: ${datos.nombre}, Admin: ${datos.esAdmin}, Comunidad: ${datos.comunidadId}")
                datos
            } else {
                println("DEBUG: No se encontró usuario en Firestore")
                null
            }
        } catch (e: Exception) {
            println("DEBUG: Error consultando Firestore: ${e.message}")
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
}