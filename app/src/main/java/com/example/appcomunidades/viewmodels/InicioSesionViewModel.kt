package com.example.appcomunidades.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcomunidades.repositorios.AuthRepositorio
import com.example.appcomunidades.repositorios.ResultadoAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

/**
 * ViewModel que maneja el estado y la lógica de la pantalla de inicio de sesión
 */
class InicioSesionViewModel : ViewModel() {

    private val authRepositorio = AuthRepositorio()

    // Estados del formulario
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _contrasenna = MutableStateFlow("")
    val contrasenna: StateFlow<String> = _contrasenna.asStateFlow()

    private val _recordarme = MutableStateFlow(false)
    val recordarme: StateFlow<Boolean> = _recordarme.asStateFlow()

    // Estados de la UI
    private val _estadoInicioSesion = MutableStateFlow<EstadoInicioSesion>(EstadoInicioSesion.Inicial)
    val estadoInicioSesion: StateFlow<EstadoInicioSesion> = _estadoInicioSesion.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    // Estado de validación del formulario en tiempo real
    val formularioValido: StateFlow<Boolean> = combine(
        _email, _contrasenna
    ) { email, contrasenna ->
        email.isNotBlank() &&
                contrasenna.isNotBlank() &&
                esEmailValido(email)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    /**
     * Funciones para actualizar los campos del formulario
     */
    fun actualizarEmail(nuevoEmail: String) {
        _email.value = nuevoEmail
        limpiarError()
    }

    fun actualizarContrasenna(nuevaContrasenna: String) {
        _contrasenna.value = nuevaContrasenna
        limpiarError()
    }

    fun actualizarRecordarme(recordar: Boolean) {
        _recordarme.value = recordar
    }

    /**
     * Valida si el email tiene formato correcto
     */
    private fun esEmailValido(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    /**
     * Inicia sesión con email y contraseña
     */
    fun iniciarSesion() {
        if (!formularioValido.value) {
            _mensajeError.value = "Por favor, completa todos los campos correctamente"
            return
        }

        _estadoInicioSesion.value = EstadoInicioSesion.Cargando

        viewModelScope.launch {
            val resultado = authRepositorio.iniciarSesion(
                email = _email.value.trim(),
                contrasenna = _contrasenna.value
            )

            when (resultado) {
                is ResultadoAuth.Exito -> {
                    _estadoInicioSesion.value = EstadoInicioSesion.Exito
                    // TODO: Guardar credenciales si recordarme está activado
                    limpiarFormulario()
                }
                is ResultadoAuth.Error -> {
                    _estadoInicioSesion.value = EstadoInicioSesion.Error
                    _mensajeError.value = procesarMensajeError(resultado.mensaje)
                }
                is ResultadoAuth.Cargando -> {
                    // Ya está en estado de carga
                }
            }
        }
    }

    /**
     * Procesa los mensajes de error de Firebase para mostrar mensajes más amigables
     */
    private fun procesarMensajeError(mensajeOriginal: String): String {
        return when {
            mensajeOriginal.contains("user-not-found") ->
                "No existe una cuenta con este email"
            mensajeOriginal.contains("wrong-password") ->
                "Contraseña incorrecta"
            mensajeOriginal.contains("invalid-email") ->
                "El formato del email no es válido"
            mensajeOriginal.contains("user-disabled") ->
                "Esta cuenta ha sido deshabilitada"
            mensajeOriginal.contains("too-many-requests") ->
                "Demasiados intentos fallidos. Intenta más tarde"
            mensajeOriginal.contains("network-request-failed") ->
                "Error de conexión. Verifica tu internet"
            else -> "Error al iniciar sesión: $mensajeOriginal"
        }
    }

    /**
     * Limpia el mensaje de error
     */
    private fun limpiarError() {
        if (_mensajeError.value != null) {
            _mensajeError.value = null
        }
        if (_estadoInicioSesion.value is EstadoInicioSesion.Error) {
            _estadoInicioSesion.value = EstadoInicioSesion.Inicial
        }
    }

    /**
     * Reinicia el estado del inicio de sesión
     */
    fun reiniciarEstado() {
        _estadoInicioSesion.value = EstadoInicioSesion.Inicial
        _mensajeError.value = null
    }

    /**
     * Limpia todos los campos del formulario
     */
    private fun limpiarFormulario() {
        _email.value = ""
        _contrasenna.value = ""
        _recordarme.value = false
    }

    /**
     * Verifica si hay un usuario ya autenticado al iniciar
     */
    fun verificarSesionExistente() {
        if (authRepositorio.estaAutenticado()) {
            _estadoInicioSesion.value = EstadoInicioSesion.Exito
        }
    }
}

/**
 * Estados posibles del proceso de inicio de sesión
 */
sealed class EstadoInicioSesion {
    object Inicial : EstadoInicioSesion()
    object Cargando : EstadoInicioSesion()
    object Exito : EstadoInicioSesion()
    object Error : EstadoInicioSesion()
}