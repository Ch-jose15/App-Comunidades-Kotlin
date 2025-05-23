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
            // 1. Crear usuario en Firebase Authentication
            val resultado = auth.createUserWithEmailAndPassword(email, contrasenna).await()
            val firebaseUser = resultado.user

            if (firebaseUser != null) {
                // 2. Generar ID único para el usuario
                val contadorUsuarios = obtenerContadorUsuarios()
                val userId = Usuario.generarId(contadorUsuarios + 1)

                // 3. Crear objeto Usuario
                val usuario = Usuario(
                    id = userId,
                    nombre = nombre,
                    email = email,
                    telefono = telefono,
                    foto_perfil = "", // Se puede agregar más tarde
                    fecha_registro = Timestamp.now(),
                    comunidad_id = comunidadId,
                    es_admin = esAdmin
                )

                // 4. Guardar usuario en Firestore
                firestore.collection("usuarios")
                    .document(userId)
                    .set(usuario.toMap())
                    .await()

                // 5. Actualizar contador de usuarios
                actualizarContadorUsuarios(contadorUsuarios + 1)

                ResultadoAuth.Exito(usuario)
            } else {
                ResultadoAuth.Error("Error al crear el usuario")
            }

        } catch (e: Exception) {
            ResultadoAuth.Error(e.message ?: "Error desconocido durante el registro")
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun iniciarSesion(email: String, contrasenna: String): ResultadoAuth {
        return try {
            val resultado = auth.signInWithEmailAndPassword(email, contrasenna).await()
            val firebaseUser = resultado.user

            if (firebaseUser != null) {
                // Obtener datos del usuario desde Firestore
                val usuario = obtenerUsuarioPorEmail(email)
                if (usuario != null) {
                    ResultadoAuth.Exito(usuario)
                } else {
                    ResultadoAuth.Error("No se encontraron datos del usuario")
                }
            } else {
                ResultadoAuth.Error("Error al iniciar sesión")
            }
        } catch (e: Exception) {
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
            val snapshot = firestore.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val documento = snapshot.documents[0]
                documento.toObject(Usuario::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
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
            // Manejar error silenciosamente
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