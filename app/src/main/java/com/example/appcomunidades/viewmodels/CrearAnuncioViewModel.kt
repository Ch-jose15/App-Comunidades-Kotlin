package com.example.appcomunidades.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcomunidades.repositorios.AnunciosRepositorio
import com.example.appcomunidades.repositorios.ResultadoOperacion
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class EstadoCrearAnuncio {
    object Inicial : EstadoCrearAnuncio()
    object Cargando : EstadoCrearAnuncio()
    object Exito : EstadoCrearAnuncio()
    data class Error(val mensaje: String) : EstadoCrearAnuncio()
}

/**
 * ViewModel SÚPER SIMPLIFICADO para crear anuncios
 */
class CrearAnuncioViewModel : ViewModel() {

    private val anunciosRepositorio = AnunciosRepositorio()

    // Estados del formulario
    private val _titulo = MutableStateFlow("")
    val titulo: StateFlow<String> = _titulo.asStateFlow()

    private val _contenido = MutableStateFlow("")
    val contenido: StateFlow<String> = _contenido.asStateFlow()

    private val _categoria = MutableStateFlow("")
    val categoria: StateFlow<String> = _categoria.asStateFlow()

    private val _esUrgente = MutableStateFlow(false)
    val esUrgente: StateFlow<Boolean> = _esUrgente.asStateFlow()

    // Estados de UI
    private val _estadoCreacion = MutableStateFlow<EstadoCrearAnuncio>(EstadoCrearAnuncio.Inicial)
    val estadoCreacion: StateFlow<EstadoCrearAnuncio> = _estadoCreacion.asStateFlow()

    private val _mostrarDialogoExito = MutableStateFlow(false)
    val mostrarDialogoExito: StateFlow<Boolean> = _mostrarDialogoExito.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    // Validación simplificada
    val formularioValido: StateFlow<Boolean> = combine(
        _titulo, _contenido, _categoria
    ) { titulo, contenido, categoria ->
        titulo.isNotBlank() &&
                contenido.isNotBlank() &&
                categoria.isNotBlank()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Contadores de caracteres
    val contadorTitulo: StateFlow<String> = _titulo.map { "${it.length}/100" }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "0/100"
    )

    val contadorContenido: StateFlow<String> = _contenido.map { "${it.length}/1000" }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "0/1000"
    )

    // Categorías disponibles
    val categoriasDisponibles = listOf(
        "General",
        "Mantenimiento",
        "Eventos",
        "Seguridad",
        "Servicios",
        "Normativas",
        "Emergencias"
    )

    /**
     * Actualizar campos del formulario
     */
    fun actualizarTitulo(nuevoTitulo: String) {
        if (nuevoTitulo.length <= 100) {
            _titulo.value = nuevoTitulo
            limpiarError()
        }
    }

    fun actualizarContenido(nuevoContenido: String) {
        if (nuevoContenido.length <= 1000) {
            _contenido.value = nuevoContenido
            limpiarError()
        }
    }

    fun actualizarCategoria(nuevaCategoria: String) {
        _categoria.value = nuevaCategoria
        limpiarError()
    }

    fun actualizarEsUrgente(urgente: Boolean) {
        _esUrgente.value = urgente
    }

    /**
     * CREAR ANUNCIO - VERSIÓN SÚPER SIMPLE
     */
    fun crearAnuncio() {
        if (!formularioValido.value) {
            _mensajeError.value = "Por favor, completa todos los campos"
            return
        }

        _estadoCreacion.value = EstadoCrearAnuncio.Cargando
        _mensajeError.value = null

        viewModelScope.launch {
            try {
                println("DEBUG: === INICIANDO CREACIÓN SIMPLE ===")

                val resultado = anunciosRepositorio.crearAnuncio(
                    titulo = _titulo.value.trim(),
                    contenido = _contenido.value.trim(),
                    categoria = _categoria.value,
                    esUrgente = _esUrgente.value
                )

                when (resultado) {
                    is ResultadoOperacion.Exito -> {
                        println("DEBUG: ✅ ¡Éxito! Anuncio creado")
                        _estadoCreacion.value = EstadoCrearAnuncio.Exito
                        _mostrarDialogoExito.value = true
                        limpiarFormulario()
                    }
                    is ResultadoOperacion.Error -> {
                        println("DEBUG: ❌ Error: ${resultado.mensaje}")
                        _estadoCreacion.value = EstadoCrearAnuncio.Error(resultado.mensaje)
                        _mensajeError.value = resultado.mensaje
                    }
                    is ResultadoOperacion.Cargando -> {
                        // Ya está en estado de carga
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: ❌ Excepción: ${e.message}")
                _estadoCreacion.value = EstadoCrearAnuncio.Error("Error inesperado")
                _mensajeError.value = "Error inesperado al crear el anuncio"
            }
        }
    }

    /**
     * Funciones de utilidad
     */
    fun obtenerColorCategoria(categoria: String): Long {
        return when (categoria.lowercase()) {
            "emergencias" -> 0xFFD32F2F
            "seguridad" -> 0xFFF57C00
            "mantenimiento" -> 0xFF1976D2
            "eventos" -> 0xFF388E3C
            "servicios" -> 0xFF7B1FA2
            "normativas" -> 0xFF455A64
            else -> 0xFF616161
        }
    }

    fun validarLongitudTitulo(): String? {
        return when {
            _titulo.value.length < 5 -> "Mínimo 5 caracteres"
            _titulo.value.length > 100 -> "Máximo 100 caracteres"
            else -> null
        }
    }

    fun validarLongitudContenido(): String? {
        return when {
            _contenido.value.length < 10 -> "Mínimo 10 caracteres"
            _contenido.value.length > 1000 -> "Máximo 1000 caracteres"
            else -> null
        }
    }

    private fun limpiarError() {
        if (_mensajeError.value != null) {
            _mensajeError.value = null
        }
        if (_estadoCreacion.value is EstadoCrearAnuncio.Error) {
            _estadoCreacion.value = EstadoCrearAnuncio.Inicial
        }
    }

    fun reiniciarEstado() {
        _estadoCreacion.value = EstadoCrearAnuncio.Inicial
        _mensajeError.value = null
    }

    private fun limpiarFormulario() {
        _titulo.value = ""
        _contenido.value = ""
        _categoria.value = ""
        _esUrgente.value = false
    }

    fun limpiarFormularioManual() {
        limpiarFormulario()
        limpiarError()
    }

    fun ocultarDialogoExito() {
        _mostrarDialogoExito.value = false
    }

    /**
     * FUNCIONES DE DEBUG TEMPORALES - ELIMINAR DESPUÉS
     */
    fun diagnosticarProblema() {
        viewModelScope.launch {
            _mensajeError.value = "✅ Sistema simplificado - No requiere diagnóstico"
        }
    }

    fun repararUsuarioFaltante() {
        viewModelScope.launch {
            _mensajeError.value = "✅ Sistema simplificado - No requiere reparación"
        }
    }
}