package com.example.appcomunidades.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcomunidades.repositorios.AuthRepositorio
import com.example.appcomunidades.repositorios.ResultadoAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class EstadoRegistro {
    object Inicial : EstadoRegistro()
    object Cargando : EstadoRegistro()
    object Exito : EstadoRegistro()
    data class Error(val mensaje: String) : EstadoRegistro()
}

class RegistroViewModel : ViewModel() {

    // Repositorio de autenticación
    private val authRepositorio = AuthRepositorio()

    // Estados del formulario
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _contrasenna = MutableStateFlow("")
    val contrasenna: StateFlow<String> = _contrasenna.asStateFlow()

    private val _confirmarContrasenna = MutableStateFlow("")
    val confirmarContrasenna: StateFlow<String> = _confirmarContrasenna.asStateFlow()

    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono.asStateFlow()

    private val _idComunidad = MutableStateFlow("")
    val idComunidad: StateFlow<String> = _idComunidad.asStateFlow()

    private val _esAdministrador = MutableStateFlow(false)
    val esAdministrador: StateFlow<Boolean> = _esAdministrador.asStateFlow()

    // Estados de UI
    private val _estadoRegistro = MutableStateFlow<EstadoRegistro>(EstadoRegistro.Inicial)
    val estadoRegistro: StateFlow<EstadoRegistro> = _estadoRegistro.asStateFlow()

    private val _mostrarDialogoExito = MutableStateFlow(false)
    val mostrarDialogoExito: StateFlow<Boolean> = _mostrarDialogoExito.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    // Validación del formulario en tiempo real
    val formularioValido: StateFlow<Boolean> = combine(
        _nombre, _email, _contrasenna, _confirmarContrasenna, _telefono, _idComunidad, _esAdministrador
    ) { valores ->
        val nombre = valores[0] as String
        val email = valores[1] as String
        val contrasenna = valores[2] as String
        val confirmarContrasenna = valores[3] as String
        val telefono = valores[4] as String
        val idComunidad = valores[5] as String
        val esAdministrador = valores[6] as Boolean

        val camposBasicosValidos = nombre.isNotBlank() &&
                email.isNotBlank() &&
                esEmailValido(email) &&
                contrasenna.length >= 6 &&
                contrasenna == confirmarContrasenna &&
                telefono.isNotBlank()

        // Si es administrador, no necesita ID de comunidad
        // Si NO es administrador (es vecino), necesita ID de comunidad
        val idComunidadValido = esAdministrador || idComunidad.isNotBlank()

        camposBasicosValidos && idComunidadValido
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Funciones para actualizar estados
    fun actualizarNombre(nuevoNombre: String) {
        _nombre.value = nuevoNombre
        limpiarError()
    }

    fun actualizarEmail(nuevoEmail: String) {
        _email.value = nuevoEmail.trim()
        limpiarError()
    }

    fun actualizarContrasenna(nuevaContrasenna: String) {
        _contrasenna.value = nuevaContrasenna
        limpiarError()
    }

    fun actualizarConfirmarContrasenna(nuevaConfirmacion: String) {
        _confirmarContrasenna.value = nuevaConfirmacion
        limpiarError()
    }

    fun actualizarTelefono(nuevoTelefono: String) {
        _telefono.value = nuevoTelefono
        limpiarError()
    }

    fun actualizarIdComunidad(nuevoId: String) {
        _idComunidad.value = nuevoId.trim()
        limpiarError()
    }

    fun actualizarEsAdministrador(esAdmin: Boolean) {
        _esAdministrador.value = esAdmin
        if (esAdmin) {
            _idComunidad.value = ""
        }
        limpiarError()
    }

    /**
     * Obtiene mensaje de error para las contraseñas
     */
    fun obtenerMensajeErrorContrasenna(): String? {
        return when {
            _contrasenna.value.isEmpty() || _confirmarContrasenna.value.isEmpty() -> null
            _contrasenna.value != _confirmarContrasenna.value -> "Las contraseñas no coinciden"
            _contrasenna.value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    /**
     * Valida si el email tiene formato correcto
     */
    private fun esEmailValido(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Registra un nuevo usuario usando el repositorio
     */
    fun registrarUsuario() {
        if (!formularioValido.value) {
            _mensajeError.value = "Por favor, completa todos los campos correctamente"
            return
        }

        _estadoRegistro.value = EstadoRegistro.Cargando
        _mensajeError.value = null

        viewModelScope.launch {
            try {
                val resultado = authRepositorio.registrarUsuario(
                    nombre = _nombre.value.trim(),
                    email = _email.value.trim(),
                    contrasenna = _contrasenna.value,
                    telefono = _telefono.value.trim(),
                    comunidadId = if (_esAdministrador.value) "" else _idComunidad.value.trim(),
                    esAdmin = _esAdministrador.value
                )

                when (resultado) {
                    is ResultadoAuth.Exito -> {
                        _estadoRegistro.value = EstadoRegistro.Exito
                        _mostrarDialogoExito.value = true
                        limpiarFormulario()
                    }
                    is ResultadoAuth.Error -> {
                        _estadoRegistro.value = EstadoRegistro.Error(resultado.mensaje)
                        _mensajeError.value = procesarMensajeError(resultado.mensaje)
                    }
                    is ResultadoAuth.Cargando -> {
                        // Ya está en estado de carga
                    }
                }
            } catch (e: Exception) {
                _estadoRegistro.value = EstadoRegistro.Error("Error inesperado: ${e.message}")
                _mensajeError.value = "Error inesperado al registrar la cuenta"
            }
        }
    }

    /**
     * Procesa los mensajes de error para mostrar mensajes más amigables
     */
    private fun procesarMensajeError(mensajeOriginal: String): String {
        return when {
            mensajeOriginal.contains("email address is already in use") ||
                    mensajeOriginal.contains("email-already-in-use") ->
                "Este correo electrónico ya está registrado"

            mensajeOriginal.contains("weak-password") ->
                "La contraseña es demasiado débil"

            mensajeOriginal.contains("invalid-email") ->
                "El formato del email no es válido"

            mensajeOriginal.contains("network") ||
                    mensajeOriginal.contains("network-request-failed") ->
                "Error de conexión. Verifica tu internet"

            mensajeOriginal.contains("too-many-requests") ->
                "Demasiados intentos. Intenta más tarde"

            else -> "Error al crear la cuenta: $mensajeOriginal"
        }
    }

    /**
     * Limpia el mensaje de error
     */
    private fun limpiarError() {
        if (_mensajeError.value != null) {
            _mensajeError.value = null
        }
        if (_estadoRegistro.value is EstadoRegistro.Error) {
            _estadoRegistro.value = EstadoRegistro.Inicial
        }
    }

    /**
     * Reinicia el estado del registro
     */
    fun reiniciarEstado() {
        _estadoRegistro.value = EstadoRegistro.Inicial
        _mensajeError.value = null
    }

    /**
     * Limpia todos los campos del formulario
     */
    private fun limpiarFormulario() {
        _nombre.value = ""
        _email.value = ""
        _contrasenna.value = ""
        _confirmarContrasenna.value = ""
        _telefono.value = ""
        _idComunidad.value = ""
        _esAdministrador.value = false
    }

    /**
     * Oculta el diálogo de éxito
     */
    fun ocultarDialogoExito() {
        _mostrarDialogoExito.value = false
    }
}