package com.example.appcomunidades.repositorios

import com.example.appcomunidades.modelos.Anuncio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

@Singleton
class AnunciosRepositorio @Inject constructor() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun crearAnuncio(
        titulo: String,
        contenido: String,
        categoria: String,
        esUrgente: Boolean
    ): ResultadoOperacion<Anuncio> {
        return try {
            println("DEBUG: === CREANDO ANUNCIO SIMPLE ===")

            // Verificar autenticación básica
            val usuarioActual = auth.currentUser
            if (usuarioActual == null) {
                return ResultadoOperacion.Error("Debes iniciar sesión para crear anuncios")
            }

            // Datos básicos del usuario
            val email = usuarioActual.email ?: "usuario@example.com"
            val nombre = usuarioActual.displayName ?: "Usuario"
            val uid = usuarioActual.uid

            val timestamp = System.currentTimeMillis()
            val anuncioId = "anuncio_${timestamp}_${UUID.randomUUID().toString().take(8)}"

            println("DEBUG: ID generado: $anuncioId")
            println("DEBUG: Usuario: $nombre ($email)")

            val anuncio = Anuncio(
                id = anuncioId,
                titulo = titulo.trim(),
                contenido = contenido.trim(),
                categoria = categoria,
                es_urgente = esUrgente,
                fecha_publicacion = Timestamp.now(),
                usuario_id = uid,
                comunidad_id = "comunidad_general",
                lectores = emptyList(),
                nombre_autor = nombre,
                fecha_formateada = "Ahora"
            )

            val datosAnuncio = mapOf(
                "id" to anuncioId,
                "titulo" to titulo.trim(),
                "contenido" to contenido.trim(),
                "categoria" to categoria,
                "es_urgente" to esUrgente,
                "fecha_publicacion" to Timestamp.now(),
                "usuario_id" to uid,
                "usuario_email" to email,
                "usuario_nombre" to nombre,
                "comunidad_id" to "comunidad_general",
                "lectores" to emptyList<String>()
            )

            // Guardar directamente en Firestore
            println("DEBUG: Guardando en Firestore...")
            firestore.collection("anuncios")
                .document(anuncioId)
                .set(datosAnuncio)
                .await()

            println("DEBUG: ✅ Anuncio guardado exitosamente")
            ResultadoOperacion.Exito(anuncio)

        } catch (e: Exception) {
            println("DEBUG: ❌ Error: ${e.message}")
            e.printStackTrace()
            ResultadoOperacion.Error("Error al crear el anuncio: ${e.message}")
        }
    }

    suspend fun obtenerTodosLosAnuncios(): ResultadoOperacion<List<Anuncio>> {
        return try {
            println("DEBUG: Obteniendo todos los anuncios...")

            val snapshot = firestore.collection("anuncios")
                .orderBy("fecha_publicacion", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            val anuncios = mutableListOf<Anuncio>()

            for (document in snapshot.documents) {
                try {
                    val anuncio = Anuncio(
                        id = document.getString("id") ?: "",
                        titulo = document.getString("titulo") ?: "",
                        contenido = document.getString("contenido") ?: "",
                        categoria = document.getString("categoria") ?: "General",
                        es_urgente = document.getBoolean("es_urgente") ?: false,
                        fecha_publicacion = document.getTimestamp("fecha_publicacion") ?: Timestamp.now(),
                        usuario_id = document.getString("usuario_id") ?: "",
                        comunidad_id = document.getString("comunidad_id") ?: "",
                        lectores = document.get("lectores") as? List<String> ?: emptyList(),
                        nombre_autor = document.getString("usuario_nombre") ?: "Usuario",
                        fecha_formateada = formatearFecha(document.getTimestamp("fecha_publicacion"))
                    )

                    if (anuncio.titulo.isNotEmpty()) {
                        anuncios.add(anuncio)
                    }
                } catch (e: Exception) {
                    println("DEBUG: Error procesando documento: ${e.message}")
                    // Continuar con el siguiente documento
                }
            }

            println("DEBUG: ✅ ${anuncios.size} anuncios obtenidos")
            ResultadoOperacion.Exito(anuncios)

        } catch (e: Exception) {
            println("DEBUG: ❌ Error obteniendo anuncios: ${e.message}")
            ResultadoOperacion.Error("Error al cargar los anuncios")
        }
    }

    //Elimina un anuncio (solo si es el autor)
    suspend fun eliminarAnuncio(anuncioId: String): ResultadoOperacion<Boolean> {
        return try {
            val usuarioActual = auth.currentUser
            if (usuarioActual == null) {
                return ResultadoOperacion.Error("Debes iniciar sesión")
            }

            // Verificar que el anuncio existe y pertenece al usuario
            val documento = firestore.collection("anuncios")
                .document(anuncioId)
                .get()
                .await()

            if (!documento.exists()) {
                return ResultadoOperacion.Error("Anuncio no encontrado")
            }

            val autorId = documento.getString("usuario_id") ?: ""
            if (autorId != usuarioActual.uid) {
                return ResultadoOperacion.Error("Solo puedes eliminar tus propios anuncios")
            }

            // Eliminar anuncio
            firestore.collection("anuncios")
                .document(anuncioId)
                .delete()
                .await()

            println("DEBUG: ✅ Anuncio eliminado: $anuncioId")
            ResultadoOperacion.Exito(true)

        } catch (e: Exception) {
            println("DEBUG: ❌ Error eliminando anuncio: ${e.message}")
            ResultadoOperacion.Error("Error al eliminar el anuncio")
        }
    }

    private fun formatearFecha(timestamp: Timestamp?): String {
        if (timestamp == null) return "Fecha desconocida"

        val ahora = System.currentTimeMillis()
        val fechaAnuncio = timestamp.toDate().time
        val diferencia = ahora - fechaAnuncio

        return when {
            diferencia < 60 * 1000 -> "Hace un momento"
            diferencia < 60 * 60 * 1000 -> {
                val minutos = diferencia / (60 * 1000)
                "Hace $minutos min"
            }
            diferencia < 24 * 60 * 60 * 1000 -> {
                val horas = diferencia / (60 * 60 * 1000)
                "Hace $horas h"
            }
            diferencia < 7 * 24 * 60 * 60 * 1000 -> {
                val dias = diferencia / (24 * 60 * 60 * 1000)
                "Hace $dias días"
            }
            else -> {
                java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    .format(timestamp.toDate())
            }
        }
    }
}

sealed class ResultadoOperacion<out T> {
    data class Exito<T>(val datos: T) : ResultadoOperacion<T>()
    data class Error(val mensaje: String) : ResultadoOperacion<Nothing>()
    object Cargando : ResultadoOperacion<Nothing>()
}