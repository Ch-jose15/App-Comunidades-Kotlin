package com.example.appcomunidades.modelos

import com.google.firebase.Timestamp

/**
 * Clase de datos que representa un usuario en la aplicación
 */
data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val foto_perfil: String = "",
    val fecha_registro: Timestamp = Timestamp.now(),
    val comunidad_id: String = "",
    val es_admin: Boolean = false
) {
    /**
     * Constructor sin parámetros requerido por Firestore
     */
    constructor() : this(
        id = "",
        nombre = "",
        email = "",
        telefono = "",
        foto_perfil = "",
        fecha_registro = Timestamp.now(),
        comunidad_id = "",
        es_admin = false
    )

    /**
     * Convierte el objeto Usuario a un Map para guardarlo en Firestore
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "nombre" to nombre,
            "email" to email,
            "telefono" to telefono,
            "foto_perfil" to foto_perfil,
            "fecha_registro" to fecha_registro,
            "comunidad_id" to comunidad_id,
            "es_admin" to es_admin
        )
    }

    /**
     * Validaciones para el registro
     */
    fun esValido(): Boolean {
        return nombre.isNotBlank() &&
                email.isNotBlank() &&
                telefono.isNotBlank() &&
                email.contains("@")
    }

    /**
     * Genera un ID único para el usuario basado en un contador
     */
    companion object {
        fun generarId(contador: Int): String {
            return "Us${contador.toString().padStart(7, '0')}a"
        }
    }
}