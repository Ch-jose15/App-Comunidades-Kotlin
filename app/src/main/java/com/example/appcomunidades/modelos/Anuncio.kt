package com.example.appcomunidades.modelos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Modelo de datos para los anuncios de la comunidad
 */
data class Anuncio(
    val id: String = "",
    val titulo: String = "",
    val contenido: String = "",
    val categoria: String = "",
    val es_urgente: Boolean = false,
    val fecha_publicacion: Timestamp = Timestamp.now(),
    val usuario_id: String = "",
    val comunidad_id: String = "",
    val lectores: List<String> = emptyList(),

    // Campos adicionales para la UI
    val nombre_autor: String = "",
    val fecha_formateada: String = ""
) {
    companion object {
        /**
         * Genera un ID único para el anuncio basado en un contador
         */
        fun generarId(contador: Int): String {
            return "As${contador.toString().padStart(7, '0')}a"
        }

        /**
         * Categorías disponibles para los anuncios
         */
        val CATEGORIAS = listOf(
            "General",
            "Mantenimiento",
            "Eventos",
            "Seguridad",
            "Servicios",
            "Normativas",
            "Emergencias"
        )

        /**
         * Convierte un DocumentSnapshot de Firestore a un objeto Anuncio
         */
        fun fromDocument(document: DocumentSnapshot): Anuncio? {
            return try {
                Anuncio(
                    id = document.getString("id") ?: "",
                    titulo = document.getString("titulo") ?: "",
                    contenido = document.getString("contenido") ?: "",
                    categoria = document.getString("categoria") ?: "",
                    es_urgente = document.getBoolean("es_urgente") ?: false,
                    fecha_publicacion = document.getTimestamp("fecha_publicacion") ?: Timestamp.now(),
                    usuario_id = document.getString("usuario_id") ?: "",
                    comunidad_id = document.getString("comunidad_id") ?: "",
                    lectores = document.get("lectores") as? List<String> ?: emptyList()
                )
            } catch (e: Exception) {
                println("Error convirtiendo documento a Anuncio: ${e.message}")
                null
            }
        }
    }

    /**
     * Convierte el objeto Anuncio a un mapa para guardar en Firestore
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "titulo" to titulo,
            "contenido" to contenido,
            "categoria" to categoria,
            "es_urgente" to es_urgente,
            "fecha_publicacion" to fecha_publicacion,
            "usuario_id" to usuario_id,
            "comunidad_id" to comunidad_id,
            "lectores" to lectores
        )
    }

    /**
     * Formatea la fecha de publicación para mostrar en la UI
     */
    fun obtenerFechaFormateada(): String {
        val ahora = System.currentTimeMillis()
        val fechaAnuncio = fecha_publicacion.toDate().time
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
                    .format(fecha_publicacion.toDate())
            }
        }
    }

    /**
     * Verifica si el anuncio fue leído por un usuario específico
     */
    fun fueLeido(usuarioId: String): Boolean {
        return lectores.contains(usuarioId)
    }

    /**
     * Crea una copia del anuncio marcándolo como leído por un usuario
     */
    fun marcarComoLeido(usuarioId: String): Anuncio {
        return if (!fueLeido(usuarioId)) {
            this.copy(lectores = lectores + usuarioId)
        } else {
            this
        }
    }

    /**
     * Obtiene el color de la categoría para la UI
     */
    fun obtenerColorCategoria(): Long {
        return when (categoria.lowercase()) {
            "emergencias" -> 0xFFD32F2F
            "seguridad" -> 0xFFF57C00
            "mantenimiento" -> 0xFF1976D2
            "eventos" -> 0xFF388E3C
            "servicios" -> 0xFF7B1FA2
            "normativas" -> 0xFF455A64
            else -> 0xFF616161 // General y otros
        }
    }

    /**
     * Valida si los datos del anuncio son válidos para guardar
     */
    fun esValido(): Boolean {
        return titulo.isNotBlank() &&
                contenido.isNotBlank() &&
                categoria.isNotBlank() &&
                usuario_id.isNotBlank() &&
                comunidad_id.isNotBlank()
    }
}
