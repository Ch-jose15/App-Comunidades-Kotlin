package com.example.appcomunidades.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

sealed class EstadoRegistro {
    object Inicial : EstadoRegistro()
    object Cargando : EstadoRegistro()
    object Exito : EstadoRegistro()
    data class Error(val mensaje: String) : EstadoRegistro()
}

class RegistroViewModel : ViewModel() {

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

    // Validación del formulario
    private val _formularioValido = MutableStateFlow(false)
    val formularioValido: StateFlow<Boolean> = _formularioValido.asStateFlow()

    init {
        // Observar cambios en los campos para validar el formulario
        viewModelScope.launch {
            combine(
                nombre,
                email,
                contrasenna,
                confirmarContrasenna,
                telefono,
                idComunidad,
                esAdministrador
            ) { valores ->
                val nombreValor = valores[0] as String
                val emailValor = valores[1] as String
                val contrasennaValor = valores[2] as String
                val confirmarContrasennaValor = valores[3] as String
                val telefonoValor = valores[4] as String
                val idComunidadValor = valores[5] as String
                val esAdministradorValor = valores[6] as Boolean

                // Validaciones básicas
                val camposBasicosValidos = nombreValor.isNotBlank() &&
                        emailValor.isNotBlank() &&
                        emailValor.contains("@") &&
                        contrasennaValor.length >= 6 &&
                        contrasennaValor == confirmarContrasennaValor &&
                        telefonoValor.isNotBlank()

                // Si es administrador, no necesita ID de comunidad
                // Si NO es administrador (es vecino), necesita ID de comunidad
                val idComunidadValido = esAdministradorValor || idComunidadValor.isNotBlank()

                camposBasicosValidos && idComunidadValido
            }.collect { esValido ->
                _formularioValido.value = esValido
            }
        }
    }

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Funciones para actualizar estados
    fun actualizarNombre(nuevoNombre: String) {
        _nombre.value = nuevoNombre
    }

    fun actualizarEmail(nuevoEmail: String) {
        _email.value = nuevoEmail.trim()
    }

    fun actualizarContrasenna(nuevaContrasenna: String) {
        _contrasenna.value = nuevaContrasenna
    }

    fun actualizarConfirmarContrasenna(nuevaConfirmacion: String) {
        _confirmarContrasenna.value = nuevaConfirmacion
    }

    fun actualizarTelefono(nuevoTelefono: String) {
        _telefono.value = nuevoTelefono
    }

    fun actualizarIdComunidad(nuevoId: String) {
        _idComunidad.value = nuevoId.trim()
    }

    fun actualizarEsAdministrador(esAdmin: Boolean) {
        _esAdministrador.value = esAdmin
        // Limpiar ID de comunidad cuando se cambia a administrador
        if (esAdmin) {
            _idComunidad.value = ""
        }
    }

    fun obtenerMensajeErrorContrasenna(): String? {
        return when {
            _contrasenna.value.isEmpty() || _confirmarContrasenna.value.isEmpty() -> null
            _contrasenna.value != _confirmarContrasenna.value -> "Las contraseñas no coinciden"
            _contrasenna.value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    suspend fun registrarUsuario() {
        try {
            _estadoRegistro.value = EstadoRegistro.Cargando
            _mensajeError.value = null

            // Validación adicional antes de proceder
            if (!_esAdministrador.value && _idComunidad.value.isBlank()) {
                _estadoRegistro.value = EstadoRegistro.Error("El ID de comunidad es obligatorio para vecinos")
                _mensajeError.value = "Debes ingresar un ID de comunidad válido"
                return
            }

            // Si no es administrador, verificar que la comunidad existe
            if (!_esAdministrador.value) {
                val comunidadExiste = verificarComunidadExiste(_idComunidad.value)
                if (!comunidadExiste) {
                    _estadoRegistro.value = EstadoRegistro.Error("La comunidad no existe")
                    _mensajeError.value = "El ID de comunidad ingresado no es válido"
                    return
                }
            }

            // Crear usuario en Firebase Auth
            auth.createUserWithEmailAndPassword(_email.value, _contrasenna.value)
                .addOnSuccessListener { authResult ->
                    val userId = authResult.user?.uid ?: return@addOnSuccessListener

                    viewModelScope.launch {
                        if (_esAdministrador.value) {
                            // Si es administrador, primero crear la comunidad
                            crearComunidadYUsuario(userId)
                        } else {
                            // Si es vecino, solo crear el usuario con la comunidad existente
                            crearUsuarioEnFirestore(userId, _idComunidad.value)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    val mensajeError = when {
                        exception.message?.contains("email address is already in use") == true ->
                            "Este correo electrónico ya está registrado"
                        exception.message?.contains("network") == true ->
                            "Error de conexión. Verifica tu internet"
                        else -> "Error al crear la cuenta: ${exception.message}"
                    }

                    _estadoRegistro.value = EstadoRegistro.Error(mensajeError)
                    _mensajeError.value = mensajeError
                }

        } catch (e: Exception) {
            _estadoRegistro.value = EstadoRegistro.Error("Error inesperado: ${e.message}")
            _mensajeError.value = "Error inesperado al registrar"
        }
    }

    private suspend fun verificarComunidadExiste(idComunidad: String): Boolean {
        return try {
            val documento = firestore.collection("comunidades")
                .document(idComunidad)
                .get()
                .await()

            documento.exists()
        } catch (e: Exception) {
            false
        }
    }

    private fun crearComunidadYUsuario(userId: String) {
        // Generar ID único para la comunidad
        val comunidadId = generarIdComunidad()

        // Crear datos de la comunidad
        val datosComunidad = hashMapOf(
            "id" to comunidadId,
            "nombre" to "Comunidad de ${_nombre.value}",
            "direccion" to "", // Se puede actualizar después
            "imagen" to "", // Se puede actualizar después
            "fecha_creacion" to Date()
        )

        // Guardar comunidad en Firestore
        firestore.collection("comunidades")
            .document(comunidadId)
            .set(datosComunidad)
            .addOnSuccessListener {
                // Una vez creada la comunidad, crear el usuario
                crearUsuarioEnFirestore(userId, comunidadId)
            }
            .addOnFailureListener { exception ->
                _estadoRegistro.value = EstadoRegistro.Error("Error al crear la comunidad")
                _mensajeError.value = "No se pudo crear la comunidad"
            }
    }

    private fun crearUsuarioEnFirestore(userId: String, comunidadId: String) {
        val datosUsuario = hashMapOf(
            "id" to userId,
            "nombre" to _nombre.value,
            "email" to _email.value,
            "telefono" to _telefono.value,
            "foto_perfil" to "",
            "fecha_registro" to Date(),
            "comunidad_id" to comunidadId,
            "es_admin" to _esAdministrador.value
        )

        firestore.collection("usuarios")
            .document(userId)
            .set(datosUsuario)
            .addOnSuccessListener {
                _estadoRegistro.value = EstadoRegistro.Exito
                _mostrarDialogoExito.value = true
            }
            .addOnFailureListener { exception ->
                // Si falla, intentar eliminar el usuario de Auth
                auth.currentUser?.delete()

                _estadoRegistro.value = EstadoRegistro.Error("Error al guardar los datos del usuario")
                _mensajeError.value = "No se pudieron guardar los datos del usuario"
            }
    }

    private fun generarIdComunidad(): String {
        // Generar ID único para la comunidad
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "Cs${timestamp}${random}a"
    }

    fun reiniciarEstado() {
        _estadoRegistro.value = EstadoRegistro.Inicial
        _mensajeError.value = null
    }

    fun ocultarDialogoExito() {
        _mostrarDialogoExito.value = false
    }
}