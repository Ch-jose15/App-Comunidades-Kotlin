package com.example.appcomunidades.repositorios

import com.example.appcomunidades.modelos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para manejar la autenticación y registro de usuarios
 * Encapsula la lógica de Firebase Auth y Firestore
 */
@Singleton
class AuthRepositorio @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Registra un nuevo usuario en Firebase Auth y guarda sus datos en Firestore
     */
    suspend fun registrarUsuario(
        nombre: String,
        email: String,
        contrasenna: String,
        telefono: String,
        comunidadId: String,
        esAdmin: Boolean
    ): ResultadoAuth {
        return try {
            println("DEBUG: === INICIANDO REGISTRO ===")
            println("DEBUG: Email: $email, EsAdmin: $esAdmin")

            // PASO 1: Crear usuario en Firebase Authentication
            println("DEBUG: Paso 1 - Creando usuario en Firebase Auth...")
            val resultado = auth.createUserWithEmailAndPassword(email, contrasenna).await()
            val firebaseUser = resultado.user

            if (firebaseUser != null) {
                val firebaseUid = firebaseUser.uid
                println("DEBUG: Usuario creado en Auth exitosamente. UID: $firebaseUid")

                // PASO 2: Generar ID único para el usuario
                println("DEBUG: Paso 2 - Obteniendo contador de usuarios...")
                val contadorUsuarios = obtenerContadorUsuarios()
                val userId = Usuario.generarId(contadorUsuarios + 1)
                println("DEBUG: ID generado: $userId")

                // PASO 3: Determinar ID de comunidad
                println("DEBUG: Paso 3 - Determinando comunidad...")
                val comunidadFinal = if (esAdmin && comunidadId.isEmpty()) {
                    println("DEBUG: Es admin sin comunidad, creando nueva...")
                    crearNuevaComunidad(nombre)
                } else {
                    if (!esAdmin && comunidadId.isNotEmpty()) {
                        println("DEBUG: Es vecino, verificando comunidad: $comunidadId")
                        val comunidadExiste = verificarComunidadExiste(comunidadId)
                        if (!comunidadExiste) {
                            println("DEBUG: ERROR - La comunidad no existe")
                            return ResultadoAuth.Error("La comunidad especificada no existe")
                        }
                        println("DEBUG: Comunidad verificada exitosamente")
                    }
                    comunidadId
                }
                println("DEBUG: Comunidad final: $comunidadFinal")

                // PASO 4: Crear objeto Usuario
                println("DEBUG: Paso 4 - Creando objeto Usuario...")
                val usuario = Usuario(
                    id = userId,
                    nombre = nombre,
                    email = email,
                    telefono = telefono,
                    foto_perfil = "",
                    fecha_registro = Timestamp.now(),
                    comunidad_id = comunidadFinal,
                    es_admin = esAdmin
                )

                // PASO 5: Guardar usuario en Firestore
                println("DEBUG: Paso 5 - Guardando usuario en Firestore...")
                try {
                    firestore.collection("usuarios")
                        .document(userId)
                        .set(usuario.toMap())
                        .await()
                    println("DEBUG: Usuario guardado en Firestore exitosamente")
                } catch (e: Exception) {
                    println("DEBUG: ERROR en Paso 5 - ${e.message}")
                    // Si falla guardar en Firestore, eliminar de Auth
                    firebaseUser.delete()
                    return ResultadoAuth.Error("Error al guardar datos del usuario: ${e.message}")
                }

                // PASO 6: Crear mapeo (OPCIONAL - puede fallar sin afectar el registro)
                println("DEBUG: Paso 6 - Creando mapeo de usuarios...")
                try {
                    firestore.collection("mapeo_usuarios")
                        .document(firebaseUid)
                        .set(mapOf(
                            "usuario_id" to userId,
                            "email" to email
                        ))
                        .await()
                    println("DEBUG: Mapeo creado exitosamente")
                } catch (e: Exception) {
                    println("DEBUG: ADVERTENCIA en Paso 6 - ${e.message} (no crítico)")
                    // No retornar error aquí, el mapeo es opcional
                }

                // PASO 7: Actualizar contador (OPCIONAL - puede fallar sin afectar el registro)
                println("DEBUG: Paso 7 - Actualizando contador...")
                try {
                    actualizarContadorUsuarios(contadorUsuarios + 1)
                    println("DEBUG: Contador actualizado exitosamente")
                } catch (e: Exception) {
                    println("DEBUG: ADVERTENCIA en Paso 7 - ${e.message} (no crítico)")
                    // No retornar error aquí, el contador es opcional
                }

                println("DEBUG: === REGISTRO COMPLETADO EXITOSAMENTE ===")
                ResultadoAuth.Exito(usuario)
            } else {
                println("DEBUG: ERROR - FirebaseUser es null")
                ResultadoAuth.Error("Error al crear el usuario en Firebase Auth")
            }

        } catch (e: Exception) {
            println("DEBUG: ERROR GENERAL en registro: ${e.message}")
            e.printStackTrace()

            ResultadoAuth.Error(e.message ?: "Error desconocido durante el registro")
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun iniciarSesion(email: String, contrasenna: String): ResultadoAuth {
        return try {
            println("DEBUG: === INICIANDO SESIÓN ===")
            println("DEBUG: Email: $email")

            val resultado = auth.signInWithEmailAndPassword(email, contrasenna).await()
            val firebaseUser = resultado.user

            if (firebaseUser != null) {
                println("DEBUG: Login en Auth exitoso")

                // Obtener datos del usuario desde Firestore
                val usuario = obtenerUsuarioPorEmail(email)
                if (usuario != null) {
                    println("DEBUG: Datos de usuario obtenidos de Firestore")
                    ResultadoAuth.Exito(usuario)
                } else {
                    println("DEBUG: ERROR - No se encontraron datos en Firestore")
                    ResultadoAuth.Error("No se encontraron datos del usuario en la base de datos")
                }
            } else {
                println("DEBUG: ERROR - FirebaseUser es null en login")
                ResultadoAuth.Error("Error al iniciar sesión: usuario no válido")
            }
        } catch (e: Exception) {
            println("DEBUG: ERROR en iniciarSesion: ${e.message}")
            e.printStackTrace()

            ResultadoAuth.Error(e.message ?: "Error desconocido durante el inicio de sesión")
        }
    }

    /**
     * Cierra la sesión del usuario actual
     */
    fun cerrarSesion() {
        auth.signOut()
    }

    /**
     * Obtiene el usuario actualmente autenticado
     */
    fun obtenerUsuarioActual(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    fun estaAutenticado(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Obtiene los datos del usuario desde Firestore por email
     */
    private suspend fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return try {
            println("DEBUG: Buscando usuario por email: $email")
            val snapshot = firestore.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val documento = snapshot.documents[0]
                println("DEBUG: Usuario encontrado en Firestore")
                documento.toObject(Usuario::class.java)
            } else {
                println("DEBUG: No se encontró usuario con email: $email")
                null
            }
        } catch (e: Exception) {
            println("DEBUG: Error obteniendo usuario por email: ${e.message}")
            null
        }
    }

    /**
     * Verifica si una comunidad existe
     */
    private suspend fun verificarComunidadExiste(comunidadId: String): Boolean {
        return try {
            val documento = firestore.collection("comunidades")
                .document(comunidadId)
                .get()
                .await()

            documento.exists()
        } catch (e: Exception) {
            println("DEBUG: Error verificando comunidad: ${e.message}")
            false
        }
    }

    /**
     * Crea una nueva comunidad para un administrador
     */
    private suspend fun crearNuevaComunidad(nombreAdmin: String): String {
        return try {
            val contadorComunidades = obtenerContadorComunidades()
            val comunidadId = "Cs${(contadorComunidades + 1).toString().padStart(7, '0')}a"

            val datosComunidad = mapOf(
                "id" to comunidadId,
                "nombre" to "Comunidad de $nombreAdmin",
                "direccion" to "",
                "imagen" to "",
                "fecha_creacion" to Timestamp.now()
            )

            firestore.collection("comunidades")
                .document(comunidadId)
                .set(datosComunidad)
                .await()

            actualizarContadorComunidades(contadorComunidades + 1)

            comunidadId
        } catch (e: Exception) {
            println("DEBUG: Error creando comunidad: ${e.message}")
            ""
        }
    }

    /**
     * Obtiene el contador actual de usuarios
     */
    private suspend fun obtenerContadorUsuarios(): Int {
        return try {
            val documento = firestore.collection("contadores")
                .document("usuarios")
                .get()
                .await()

            if (documento.exists()) {
                documento.getLong("cantidad")?.toInt() ?: 0
            } else {
                // Si no existe, crear el documento contador
                firestore.collection("contadores")
                    .document("usuarios")
                    .set(mapOf("cantidad" to 0))
                    .await()
                0
            }
        } catch (e: Exception) {
            println("DEBUG: Error obteniendo contador usuarios: ${e.message}")
            0
        }
    }

    /**
     * Actualiza el contador de usuarios
     */
    private suspend fun actualizarContadorUsuarios(nuevoValor: Int) {
        try {
            firestore.collection("contadores")
                .document("usuarios")
                .set(mapOf("cantidad" to nuevoValor))
                .await()
        } catch (e: Exception) {
            println("DEBUG: Error actualizando contador usuarios: ${e.message}")
        }
    }

    /**
     * Obtiene el contador actual de comunidades
     */
    private suspend fun obtenerContadorComunidades(): Int {
        return try {
            val documento = firestore.collection("contadores")
                .document("comunidades")
                .get()
                .await()

            if (documento.exists()) {
                documento.getLong("cantidad")?.toInt() ?: 0
            } else {
                firestore.collection("contadores")
                    .document("comunidades")
                    .set(mapOf("cantidad" to 0))
                    .await()
                0
            }
        } catch (e: Exception) {
            println("DEBUG: Error obteniendo contador comunidades: ${e.message}")
            0
        }
    }

    /**
     * Actualiza el contador de comunidades
     */
    private suspend fun actualizarContadorComunidades(nuevoValor: Int) {
        try {
            firestore.collection("contadores")
                .document("comunidades")
                .set(mapOf("cantidad" to nuevoValor))
                .await()
        } catch (e: Exception) {
            println("DEBUG: Error actualizando contador comunidades: ${e.message}")
        }
    }
}

/**
 * Clase sellada para representar el resultado de operaciones de autenticación
 */
sealed class ResultadoAuth {
    data class Exito(val usuario: Usuario) : ResultadoAuth()
    data class Error(val mensaje: String) : ResultadoAuth()
    object Cargando : ResultadoAuth()
}