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
 * ViewModel que maneja el estado y la lógica de la pantalla de registro
 */
class RegistroViewModel : ViewModel() {

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

    // Estados de la UI
    private val _estadoRegistro = MutableStateFlow<EstadoRegistro>(EstadoRegistro.Inicial)
    val estadoRegistro: StateFlow<EstadoRegistro> = _estadoRegistro.asStateFlow()

    private val _mostrarDialogoExito = MutableStateFlow(false)
    val mostrarDialogoExito: StateFlow<Boolean> = _mostrarDialogoExito.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    // Estado de validación del formulario en tiempo real
    val formularioValido: StateFlow<Boolean> = combine(
        _nombre, _email, _contrasenna, _confirmarContrasenna, _telefono
    ) { nombre, email, contrasenna, confirmarContrasenna, telefono ->
        nombre.isNotBlank() &&
                email.isNotBlank() &&
                contrasenna.isNotBlank() &&
                confirmarContrasenna.isNotBlank() &&
                telefono.isNotBlank() &&
                contrasenna == confirmarContrasenna &&
                esEmailValido(email) &&
                esContrasennaValida(contrasenna)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    /**
     * Funciones para actualizar los campos del formulario
     */
    fun actualizarNombre(nuevoNombre: String) {
        _nombre.value = nuevoNombre
        limpiarError()
    }

    fun actualizarEmail(nuevoEmail: String) {
        _email.value = nuevoEmail
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
        _idComunidad.value = nuevoId
    }

    fun actualizarEsAdministrador(esAdmin: Boolean) {
        _esAdministrador.value = esAdmin
    }

    /**
     * Valida si el formulario está completo y es válido
     */
    fun formularioEsValido(): Boolean {
        return _nombre.value.isNotBlank() &&
                _email.value.isNotBlank() &&
                _contrasenna.value.isNotBlank() &&
                _confirmarContrasenna.value.isNotBlank() &&
                _telefono.value.isNotBlank() &&
                _contrasenna.value == _confirmarContrasenna.value &&
                esEmailValido(_email.value) &&
                esContrasennaValida(_contrasenna.value)
    }

    /**
     * Valida si el email tiene formato correcto
     */
    private fun esEmailValido(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    /**
     * Valida si la contraseña cumple los requisitos mínimos
     */
    private fun esContrasennaValida(contrasenna: String): Boolean {
        return contrasenna.length >= 6
    }

    /**
     * Verifica si las contraseñas coinciden
     */
    fun contrasennasCoincidenYSonValidas(): Boolean {
        return _contrasenna.value == _confirmarContrasenna.value &&
                _contrasenna.value.isNotEmpty() &&
                _confirmarContrasenna.value.isNotEmpty()
    }

    /**
     * Obtiene el mensaje de error de validación de contraseñas
     */
    fun obtenerMensajeErrorContrasenna(): String? {
        return when {
            _confirmarContrasenna.value.isEmpty() -> null
            _contrasenna.value != _confirmarContrasenna.value -> "Las contraseñas no coinciden"
            _contrasenna.value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    /**
     * Registra un nuevo usuario
     */
    fun registrarUsuario() {
        if (!formularioEsValido()) {
            _mensajeError.value = "Por favor, completa todos los campos correctamente"
            return
        }

        _estadoRegistro.value = EstadoRegistro.Cargando

        viewModelScope.launch {
            val resultado = authRepositorio.registrarUsuario(
                nombre = _nombre.value.trim(),
                email = _email.value.trim(),
                contrasenna = _contrasenna.value,
                telefono = _telefono.value.trim(),
                comunidadId = _idComunidad.value.trim(),
                esAdmin = _esAdministrador.value
            )

            when (resultado) {
                is ResultadoAuth.Exito -> {
                    _estadoRegistro.value = EstadoRegistro.Exito
                    _mostrarDialogoExito.value = true
                    limpiarFormulario()
                }
                is ResultadoAuth.Error -> {
                    _estadoRegistro.value = EstadoRegistro.Error
                    _mensajeError.value = resultado.mensaje
                }
                is ResultadoAuth.Cargando -> {
                    // Ya está en estado de carga
                }
            }
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
        _mostrarDialogoExito.value = false
    }

    /**
     * Oculta el diálogo de éxito
     */
    fun ocultarDialogoExito() {
        _mostrarDialogoExito.value = false
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
     * Obtiene el porcentaje de progreso del formulario (para mostrar en UI si se desea)
     */
    fun obtenerProgresoFormulario(): Float {
        val camposCompletos = listOf(
            _nombre.value.isNotBlank(),
            _email.value.isNotBlank(),
            _contrasenna.value.isNotBlank(),
            _confirmarContrasenna.value.isNotBlank(),
            _telefono.value.isNotBlank()
        ).count { it }

        return camposCompletos / 5f
    }
}

/**
 * Estados posibles del proceso de registro
 */
sealed class EstadoRegistro {
    object Inicial : EstadoRegistro()
    object Cargando : EstadoRegistro()
    object Exito : EstadoRegistro()
    object Error : EstadoRegistro()
}