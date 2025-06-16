package com.example.appcomunidades.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Estados posibles del proceso de recuperación de contraseña
 */
sealed class EstadoRecuperacion {
    object Inicial : EstadoRecuperacion()
    object Cargando : EstadoRecuperacion()
    object Exito : EstadoRecuperacion()
    object Error : EstadoRecuperacion()
}

/**
 * ViewModel que maneja el estado y la lógica de recuperación de contraseña
 */
class RecuperarContrasennaViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estados del formulario
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // Estados de la UI
    private val _estadoRecuperacion = MutableStateFlow<EstadoRecuperacion>(EstadoRecuperacion.Inicial)
    val estadoRecuperacion: StateFlow<EstadoRecuperacion> = _estadoRecuperacion.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _mostrarDialogoExito = MutableStateFlow(false)
    val mostrarDialogoExito: StateFlow<Boolean> = _mostrarDialogoExito.asStateFlow()

    // Validación del email en tiempo real
    val emailEsValido: StateFlow<Boolean> = _email.map { email ->
        esEmailValido(email)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    /**
     * Actualiza el email introducido
     */
    fun actualizarEmail(nuevoEmail: String) {
        _email.value = nuevoEmail.trim()
        limpiarError()
    }

    /**
     * Valida si el email tiene formato correcto
     */
    private fun esEmailValido(email: String): Boolean {
        return if (email.isBlank()) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    /**
     * Envía el email de recuperación de contraseña
     */
    fun enviarRecuperacion() {
        if (!emailEsValido.value) {
            _mensajeError.value = "Por favor, introduce un email válido"
            return
        }

        _estadoRecuperacion.value = EstadoRecuperacion.Cargando
        _mensajeError.value = null

        viewModelScope.launch {
            try {
                println("DEBUG: === ENVIANDO RECUPERACIÓN DE CONTRASEÑA ===")
                println("DEBUG: Email: ${_email.value}")

                // Verificar si el email existe en Firebase Auth
                val emailExiste = verificarEmailExiste(_email.value)

                if (!emailExiste) {
                    println("DEBUG: Email no registrado: ${_email.value}")
                    _estadoRecuperacion.value = EstadoRecuperacion.Error
                    _mensajeError.value = "No existe una cuenta asociada a este correo electrónico"
                    return@launch
                }

                // Enviar email de recuperación
                auth.sendPasswordResetEmail(_email.value).await()

                println("DEBUG: ✅ Email de recuperación enviado exitosamente")
                _estadoRecuperacion.value = EstadoRecuperacion.Exito
                _mostrarDialogoExito.value = true

            } catch (e: Exception) {
                println("DEBUG: ❌ Error enviando recuperación: ${e.message}")
                e.printStackTrace()

                _estadoRecuperacion.value = EstadoRecuperacion.Error
                _mensajeError.value = procesarMensajeError(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Verifica si el email existe en Firebase Auth de forma segura
     */
    private suspend fun verificarEmailExiste(email: String): Boolean {
        return try {
            // Intentar obtener métodos de inicio de sesión para el email
            val metodos = auth.fetchSignInMethodsForEmail(email).await()
            metodos.signInMethods?.isNotEmpty() == true
        } catch (e: Exception) {
            println("DEBUG: Error verificando email: ${e.message}")
            // Si hay error verificando, asumir que puede existir para no bloquear
            true
        }
    }

    /**
     * Procesa los mensajes de error de Firebase para mostrar mensajes más amigables
     */
    private fun procesarMensajeError(mensajeOriginal: String): String {
        return when {
            mensajeOriginal.contains("user-not-found") ||
                    mensajeOriginal.contains("email-not-found") ->
                "No existe una cuenta asociada a este correo electrónico"

            mensajeOriginal.contains("invalid-email") ->
                "El formato del email no es válido"

            mensajeOriginal.contains("network-request-failed") ||
                    mensajeOriginal.contains("network") ->
                "Error de conexión. Verifica tu internet e intenta nuevamente"

            mensajeOriginal.contains("too-many-requests") ->
                "Demasiados intentos. Espera unos minutos antes de intentar nuevamente"

            mensajeOriginal.contains("internal-error") ->
                "Error interno del servidor. Intenta más tarde"

            mensajeOriginal.contains("quota-exceeded") ->
                "Se ha superado el límite de emails de recuperación. Intenta más tarde"

            else -> "Error al enviar el correo de recuperación. Intenta nuevamente"
        }
    }

    /**
     * Limpia el mensaje de error
     */
    private fun limpiarError() {
        if (_mensajeError.value != null) {
            _mensajeError.value = null
        }
        if (_estadoRecuperacion.value is EstadoRecuperacion.Error) {
            _estadoRecuperacion.value = EstadoRecuperacion.Inicial
        }
    }

    /**
     * Reinicia el estado de la recuperación
     */
    fun reiniciarEstado() {
        _estadoRecuperacion.value = EstadoRecuperacion.Inicial
        _mensajeError.value = null
    }

    /**
     * Oculta el diálogo de éxito
     */
    fun ocultarDialogoExito() {
        _mostrarDialogoExito.value = false
    }

    /**
     * Limpia completamente el formulario (útil al navegar)
     */
    fun limpiarFormulario() {
        _email.value = ""
        reiniciarEstado()
        ocultarDialogoExito()
    }

    /**
     * Funciones de diagnóstico para debugging
     */
    fun diagnosticarConexion() {
        viewModelScope.launch {
            try {
                println("DEBUG: === DIAGNÓSTICO DE CONEXIÓN ===")

                // Verificar conexión básica con Firebase
                val usuario = auth.currentUser
                println("DEBUG: Usuario actual: ${usuario?.email ?: "No autenticado"}")

                // Intentar una operación simple
                auth.fetchSignInMethodsForEmail("test@test.com").await()
                println("DEBUG: ✅ Conexión con Firebase Auth OK")

                _mensajeError.value = "✅ Conexión verificada - Firebase está funcionando correctamente"

            } catch (e: Exception) {
                println("DEBUG: ❌ Error de conexión: ${e.message}")
                _mensajeError.value = "❌ Error de conexión: ${procesarMensajeError(e.message ?: "")}"
            }
        }
    }

    /**
     * Obtiene información sobre el estado actual del sistema
     */
    fun obtenerEstadoSistema(): String {
        return buildString {
            appendLine("=== ESTADO DEL SISTEMA ===")
            appendLine("Email actual: '${_email.value}'")
            appendLine("Email válido: ${emailEsValido.value}")
            appendLine("Estado: ${_estadoRecuperacion.value}")
            appendLine("Usuario Auth: ${auth.currentUser?.email ?: "No autenticado"}")
            appendLine("Error actual: ${_mensajeError.value ?: "Ninguno"}")
            appendLine("Diálogo éxito: ${_mostrarDialogoExito.value}")
        }
    }

    /**
     * Función de testing para probar el flujo completo
     */
    fun probarFlujoRecuperacion(emailTest: String = "test@example.com") {
        viewModelScope.launch {
            println("DEBUG: === PROBANDO FLUJO DE RECUPERACIÓN ===")
            println("DEBUG: Email de prueba: $emailTest")

            _email.value = emailTest

            if (esEmailValido(emailTest)) {
                println("DEBUG: ✅ Email válido")
                enviarRecuperacion()
            } else {
                println("DEBUG: ❌ Email inválido")
                _mensajeError.value = "Email de prueba inválido"
            }
        }
    }
}