package com.example.appcomunidades.repositorios

import com.example.appcomunidades.modelos.Incidencia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

/**
 * Repositorio para manejo de incidencias
 * Basado en el AnunciosRepositorio pero adaptado para incidencias
 */
@Singleton
class IncidenciasRepositorio @Inject constructor() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Crea una nueva incidencia
     */
    suspend fun crearIncidencia(
        titulo: String,
        descripcion: String,
        categoria: String,
        prioridad: String
    ): ResultadoOperacion<Incidencia> {
        return try {
            println("DEBUG: === CREANDO INCIDENCIA ===")

            // Verificar autenticación
            val usuarioActual = auth.currentUser
            if (usuarioActual == null) {
                return ResultadoOperacion.Error("Debes iniciar sesión para crear incidencias")
            }

            // Datos básicos del usuario
            val email = usuarioActual.email ?: "usuario@example.com"
            val nombre = usuarioActual.displayName ?: "Usuario"
            val uid = usuarioActual.uid

            // Generar ID único
            val timestamp = System.currentTimeMillis()
            val incidenciaId = "incidencia_${timestamp}_${UUID.randomUUID().toString().take(8)}"

            println("DEBUG: ID generado: $incidenciaId")

            // Obtener comunidad del usuario
            val comunidadUsuario = obtenerComunidadRealDelUsuario(email)

            println("DEBUG: Usuario: $nombre ($email)")
            println("DEBUG: Comunidad: $comunidadUsuario")
            println("DEBUG: Prioridad: $prioridad")

            // Crear incidencia
            val incidencia = Incidencia(
                id = incidenciaId,
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                categoria = categoria,
                prioridad = prioridad,
                estado = "pendiente", // Estado inicial
                fecha_creacion = Timestamp.now(),
                fecha_actualizacion = Timestamp.now(),
                usuario_id = uid,
                comunidad_id = comunidadUsuario,
                imagenes = emptyList(),
                nombre_autor = nombre,
                fecha_formateada = "Ahora",
                color_prioridad = Incidencia.obtenerColorPrioridad(prioridad),
                color_estado = Incidencia.obtenerColorEstado("pendiente"),
                icono_categoria = Incidencia.obtenerIconoCategoria(categoria)
            )

            // Datos para Firestore
            val datosIncidencia = mapOf(
                "id" to incidenciaId,
                "titulo" to titulo.trim(),
                "descripcion" to descripcion.trim(),
                "categoria" to categoria,
                "prioridad" to prioridad,
                "estado" to "pendiente",
                "fecha_creacion" to Timestamp.now(),
                "fecha_actualizacion" to Timestamp.now(),
                "usuario_id" to uid,
                "usuario_email" to email,
                "usuario_nombre" to nombre,
                "comunidad_id" to comunidadUsuario,
                "imagenes" to emptyList<String>()
            )

            println("DEBUG: Guardando en Firestore...")
            firestore.collection("incidencias")
                .document(incidenciaId)
                .set(datosIncidencia)
                .await()

            println("DEBUG: ✅ Incidencia creada exitosamente")
            ResultadoOperacion.Exito(incidencia)

        } catch (e: Exception) {
            println("DEBUG: ❌ Error: ${e.message}")
            e.printStackTrace()
            ResultadoOperacion.Error("Error al crear la incidencia: ${e.message}")
        }
    }

    /**
     * Obtiene todas las incidencias (sistema global)
     */
    suspend fun obtenerTodasLasIncidencias(): ResultadoOperacion<List<Incidencia>> {
        return try {
            println("DEBUG: === OBTENIENDO TODAS LAS INCIDENCIAS ===")

            val snapshot = firestore.collection("incidencias")
                .orderBy("fecha_creacion", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            val incidencias = mutableListOf<Incidencia>()

            for (document in snapshot.documents) {
                try {
                    val incidencia = Incidencia(
                        id = document.getString("id") ?: "",
                        titulo = document.getString("titulo") ?: "",
                        descripcion = document.getString("descripcion") ?: "",
                        categoria = document.getString("categoria") ?: "Otros",
                        prioridad = document.getString("prioridad") ?: "baja",
                        estado = document.getString("estado") ?: "pendiente",
                        fecha_creacion = document.getTimestamp("fecha_creacion") ?: Timestamp.now(),
                        fecha_actualizacion = document.getTimestamp("fecha_actualizacion") ?: Timestamp.now(),
                        usuario_id = document.getString("usuario_id") ?: "",
                        comunidad_id = document.getString("comunidad_id") ?: "",
                        imagenes = document.get("imagenes") as? List<String> ?: emptyList(),
                        nombre_autor = document.getString("usuario_nombre") ?: "Usuario",
                        fecha_formateada = formatearFecha(document.getTimestamp("fecha_creacion")),
                        color_prioridad = Incidencia.obtenerColorPrioridad(
                            document.getString("prioridad") ?: "baja"
                        ),
                        color_estado = Incidencia.obtenerColorEstado(
                            document.getString("estado") ?: "pendiente"
                        ),
                        icono_categoria = Incidencia.obtenerIconoCategoria(
                            document.getString("categoria") ?: "Otros"
                        )
                    )

                    if (incidencia.titulo.isNotEmpty()) {
                        incidencias.add(incidencia)
                    }
                } catch (e: Exception) {
                    println("DEBUG: Error procesando documento: ${e.message}")
                }
            }

            println("DEBUG: ✅ ${incidencias.size} incidencias obtenidas")
            ResultadoOperacion.Exito(incidencias)

        } catch (e: Exception) {
            println("DEBUG: ❌ Error obteniendo incidencias: ${e.message}")
            ResultadoOperacion.Error("Error al cargar las incidencias")
        }
    }

    /**
     * Obtiene incidencias por comunidad
     */
    suspend fun obtenerIncidenciasPorComunidad(comunidadId: String): ResultadoOperacion<List<Incidencia>> {
        return try {
            println("DEBUG: === OBTENIENDO INCIDENCIAS DE COMUNIDAD: $comunidadId ===")

            val snapshot = firestore.collection("incidencias")
                .whereEqualTo("comunidad_id", comunidadId)
                .orderBy("fecha_creacion", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            val incidencias = mutableListOf<Incidencia>()

            for (document in snapshot.documents) {
                try {
                    val incidencia = Incidencia(
                        id = document.getString("id") ?: "",
                        titulo = document.getString("titulo") ?: "",
                        descripcion = document.getString("descripcion") ?: "",
                        categoria = document.getString("categoria") ?: "Otros",
                        prioridad = document.getString("prioridad") ?: "baja",
                        estado = document.getString("estado") ?: "pendiente",
                        fecha_creacion = document.getTimestamp("fecha_creacion") ?: Timestamp.now(),
                        fecha_actualizacion = document.getTimestamp("fecha_actualizacion") ?: Timestamp.now(),
                        usuario_id = document.getString("usuario_id") ?: "",
                        comunidad_id = document.getString("comunidad_id") ?: "",
                        imagenes = document.get("imagenes") as? List<String> ?: emptyList(),
                        nombre_autor = document.getString("usuario_nombre") ?: "Usuario",
                        fecha_formateada = formatearFecha(document.getTimestamp("fecha_creacion")),
                        color_prioridad = Incidencia.obtenerColorPrioridad(
                            document.getString("prioridad") ?: "baja"
                        ),
                        color_estado = Incidencia.obtenerColorEstado(
                            document.getString("estado") ?: "pendiente"
                        ),
                        icono_categoria = Incidencia.obtenerIconoCategoria(
                            document.getString("categoria") ?: "Otros"
                        )
                    )

                    if (incidencia.titulo.isNotEmpty()) {
                        incidencias.add(incidencia)
                    }
                } catch (e: Exception) {
                    println("DEBUG: Error procesando documento: ${e.message}")
                }
            }

            println("DEBUG: ✅ ${incidencias.size} incidencias de la comunidad obtenidas")
            ResultadoOperacion.Exito(incidencias)

        } catch (e: Exception) {
            println("DEBUG: ❌ Error obteniendo incidencias por comunidad: ${e.message}")
            ResultadoOperacion.Error("Error al cargar incidencias de la comunidad")
        }
    }

    /**
     * Actualiza el estado de una incidencia
     */
    suspend fun actualizarEstadoIncidencia(
        incidenciaId: String,
        nuevoEstado: String
    ): ResultadoOperacion<Boolean> {
        return try {
            println("DEBUG: === ACTUALIZANDO ESTADO INCIDENCIA: $incidenciaId ===")

            val usuarioActual = auth.currentUser
            if (usuarioActual == null) {
                return ResultadoOperacion.Error("Debes iniciar sesión")
            }

            // Verificar que la incidencia existe
            val documento = firestore.collection("incidencias")
                .document(incidenciaId)
                .get()
                .await()

            if (!documento.exists()) {
                return ResultadoOperacion.Error("Incidencia no encontrada")
            }

            // Actualizar estado y fecha de actualización
            firestore.collection("incidencias")
                .document(incidenciaId)
                .update(
                    mapOf(
                        "estado" to nuevoEstado,
                        "fecha_actualizacion" to Timestamp.now()
                    )
                )
                .await()

            println("DEBUG: ✅ Estado actualizado a: $nuevoEstado")
            ResultadoOperacion.Exito(true)

        } catch (e: Exception) {
            println("DEBUG: ❌ Error actualizando estado: ${e.message}")
            ResultadoOperacion.Error("Error al actualizar el estado")
        }
    }

    /**
     * Elimina una incidencia (solo el autor o admin)
     */
    suspend fun eliminarIncidencia(incidenciaId: String): ResultadoOperacion<Boolean> {
        return try {
            println("DEBUG: === ELIMINANDO INCIDENCIA: $incidenciaId ===")

            val usuarioActual = auth.currentUser
            if (usuarioActual == null) {
                return ResultadoOperacion.Error("Debes iniciar sesión")
            }

            // Verificar que la incidencia existe y pertenece al usuario
            val documento = firestore.collection("incidencias")
                .document(incidenciaId)
                .get()
                .await()

            if (!documento.exists()) {
                return ResultadoOperacion.Error("Incidencia no encontrada")
            }

            val autorId = documento.getString("usuario_id") ?: ""
            if (autorId != usuarioActual.uid) {
                return ResultadoOperacion.Error("Solo puedes eliminar tus propias incidencias")
            }

            // Eliminar incidencia
            firestore.collection("incidencias")
                .document(incidenciaId)
                .delete()
                .await()

            println("DEBUG: ✅ Incidencia eliminada: $incidenciaId")
            ResultadoOperacion.Exito(true)

        } catch (e: Exception) {
            println("DEBUG: ❌ Error eliminando incidencia: ${e.message}")
            ResultadoOperacion.Error("Error al eliminar la incidencia")
        }
    }

    /**
     * Obtiene la comunidad del usuario actual
     */
    suspend fun obtenerComunidadUsuarioActual(): String {
        return try {
            val usuarioActual = auth.currentUser
            if (usuarioActual == null) {
                return ""
            }

            val email = usuarioActual.email ?: ""
            if (email.isEmpty()) {
                return ""
            }

            obtenerComunidadRealDelUsuario(email)
        } catch (e: Exception) {
            println("DEBUG: Error obteniendo comunidad usuario actual: ${e.message}")
            ""
        }
    }

    /**
     * Obtiene la comunidad real del usuario desde Firestore
     */
    private suspend fun obtenerComunidadRealDelUsuario(email: String): String {
        return try {
            println("DEBUG: Buscando comunidad del usuario: $email")

            val snapshot = firestore.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val documento = snapshot.documents[0]
                val comunidadId = documento.getString("comunidad_id") ?: ""
                println("DEBUG: Comunidad encontrada: $comunidadId")

                if (comunidadId.isNotEmpty()) {
                    return comunidadId
                } else {
                    println("DEBUG: Usuario sin comunidad asignada")
                    return "Sin Comunidad"
                }
            } else {
                println("DEBUG: Usuario no encontrado en Firestore")
                return "Usuario No Registrado"
            }
        } catch (e: Exception) {
            println("DEBUG: Error obteniendo comunidad: ${e.message}")
            return "Error Comunidad"
        }
    }

    /**
     * Formatea fechas de manera amigable
     */
    private fun formatearFecha(timestamp: Timestamp?): String {
        if (timestamp == null) return "Fecha desconocida"

        val ahora = System.currentTimeMillis()
        val fechaIncidencia = timestamp.toDate().time
        val diferencia = ahora - fechaIncidencia

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