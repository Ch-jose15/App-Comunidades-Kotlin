package com.example.appcomunidades.modelos

import com.google.firebase.Timestamp

/**
 * Modelo de datos para las incidencias
 */
data class Incidencia(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val prioridad: String = "",
    val estado: String = "",
    val fecha_creacion: Timestamp = Timestamp.now(),
    val fecha_actualizacion: Timestamp = Timestamp.now(),
    val usuario_id: String = "",
    val comunidad_id: String = "",
    val imagenes: List<String> = emptyList(),
    val nombre_autor: String = "",
    val fecha_formateada: String = "",
    val color_prioridad: Long = 0xFF9BA8AB,
    val color_estado: Long = 0xFF9BA8AB,
    val icono_categoria: String = "🔧"
) {
    /**
     * Convierte la incidencia a un Map para Firestore
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "titulo" to titulo,
            "descripcion" to descripcion,
            "categoria" to categoria,
            "prioridad" to prioridad,
            "estado" to estado,
            "fecha_creacion" to fecha_creacion,
            "fecha_actualizacion" to fecha_actualizacion,
            "usuario_id" to usuario_id,
            "comunidad_id" to comunidad_id,
            "imagenes" to imagenes
        )
    }

    companion object {
        /**
         * Categorías disponibles para incidencias
         */
        val CATEGORIAS_DISPONIBLES = listOf(
            "Mantenimiento",
            "Electricidad",
            "Fontanería",
            "Limpieza",
            "Seguridad",
            "Ruidos",
            "Ascensor",
            "Jardines",
            "Garaje",
            "Otros"
        )

        /**
         * Prioridades disponibles
         */
        val PRIORIDADES_DISPONIBLES = listOf(
            "baja",
            "media",
            "alta"
        )

        /**
         * Estados disponibles
         */
        val ESTADOS_DISPONIBLES = listOf(
            "pendiente",
            "en proceso",
            "resuelta"
        )

        /**
         * Obtiene el color según la prioridad
         */
        fun obtenerColorPrioridad(prioridad: String): Long {
            return when (prioridad.lowercase()) {
                "alta" -> 0xFF11212D
                "media" -> 0xFF253745
                "baja" -> 0xFF4A5C6A
                else -> 0xFF9BA8AB
            }
        }

        /**
         * Obtiene el color según el estado
         */
        fun obtenerColorEstado(estado: String): Long {
            return when (estado.lowercase()) {
                "resuelta" -> 0xFF4A5C6A
                "en proceso" -> 0xFF253745
                "pendiente" -> 0xFF9BA8AB
                else -> 0xFF9BA8AB
            }
        }

        /**
         * Obtiene el icono según la categoría
         */
        fun obtenerIconoCategoria(categoria: String): String {
            return when (categoria.lowercase()) {
                "mantenimiento" -> "🔧"
                "electricidad" -> "⚡"
                "fontanería" -> "🚰"
                "limpieza" -> "🧹"
                "seguridad" -> "🔒"
                "ruidos" -> "🔊"
                "ascensor" -> "🛗"
                "jardines" -> "🌱"
                "garaje" -> "🚗"
                else -> "📋"
            }
        }

        /**
         * Genera un ID único para la incidencia
         */
        fun generarId(contador: Int): String {
            return "Is${contador.toString().padStart(7, '0')}a"
        }

        /**
         * Valida si una incidencia tiene los datos mínimos requeridos
         */
        fun esValida(incidencia: Incidencia): Boolean {
            return incidencia.titulo.isNotBlank() &&
                    incidencia.descripcion.isNotBlank() &&
                    incidencia.categoria.isNotBlank() &&
                    incidencia.prioridad in PRIORIDADES_DISPONIBLES &&
                    incidencia.estado in ESTADOS_DISPONIBLES
        }

        /**
         * Crea una incidencia con valores por defecto
         */
        fun crear(
            titulo: String,
            descripcion: String,
            categoria: String,
            prioridad: String,
            usuarioId: String,
            comunidadId: String,
            nombreAutor: String = "Usuario"
        ): Incidencia {
            val ahora = Timestamp.now()
            return Incidencia(
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                categoria = categoria,
                prioridad = prioridad,
                estado = "pendiente",
                fecha_creacion = ahora,
                fecha_actualizacion = ahora,
                usuario_id = usuarioId,
                comunidad_id = comunidadId,
                nombre_autor = nombreAutor,
                fecha_formateada = "Ahora",
                color_prioridad = obtenerColorPrioridad(prioridad),
                color_estado = obtenerColorEstado("pendiente"),
                icono_categoria = obtenerIconoCategoria(categoria)
            )
        }
    }
}