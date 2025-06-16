package com.example.appcomunidades.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcomunidades.repositorios.IncidenciasRepositorio
import com.example.appcomunidades.repositorios.ResultadoOperacion
import com.example.appcomunidades.modelos.Incidencia
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Estados para crear incidencias
 */
sealed class EstadoCrearIncidencia {
    object Inicial : EstadoCrearIncidencia()
    object Cargando : EstadoCrearIncidencia()
    object Exito : EstadoCrearIncidencia()
    data class Error(val mensaje: String) : EstadoCrearIncidencia()
}

/**
 * ViewModel para crear incidencias
 * Basado en CrearAnuncioViewModel pero adaptado para incidencias
 */
class CrearIncidenciaViewModel : ViewModel() {

    private val incidenciasRepositorio = IncidenciasRepositorio()

    // Estados del formulario
    private val _titulo = MutableStateFlow("")
    val titulo: StateFlow<String> = _titulo.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion.asStateFlow()

    private val _categoria = MutableStateFlow("")
    val categoria: StateFlow<String> = _categoria.asStateFlow()

    private val _prioridad = MutableStateFlow("media")
    val prioridad: StateFlow<String> = _prioridad.asStateFlow()

    // Estados de UI
    private val _estadoCreacion = MutableStateFlow<EstadoCrearIncidencia>(EstadoCrearIncidencia.Inicial)
    val estadoCreacion: StateFlow<EstadoCrearIncidencia> = _estadoCreacion.asStateFlow()

    private val _mostrarDialogoExito = MutableStateFlow(false)
    val mostrarDialogoExito: StateFlow<Boolean> = _mostrarDialogoExito.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    // Validación del formulario
    val formularioValido: StateFlow<Boolean> = combine(
        _titulo, _descripcion, _categoria, _prioridad
    ) { titulo, descripcion, categoria, prioridad ->
        titulo.isNotBlank() &&
                descripcion.isNotBlank() &&
                categoria.isNotBlank() &&
                prioridad in Incidencia.PRIORIDADES_DISPONIBLES
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Contadores de caracteres
    val contadorTitulo: StateFlow<String> = _titulo.map { "${it.length}/80" }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "0/80"
    )

    val contadorDescripcion: StateFlow<String> = _descripcion.map { "${it.length}/500" }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "0/500"
    )

    // Categorías y prioridades disponibles
    val categoriasDisponibles = Incidencia.CATEGORIAS_DISPONIBLES
    val prioridadesDisponibles = Incidencia.PRIORIDADES_DISPONIBLES

    /**
     * Actualizar campos del formulario
     */
    fun actualizarTitulo(nuevoTitulo: String) {
        if (nuevoTitulo.length <= 80) {
            _titulo.value = nuevoTitulo
            limpiarError()
        }
    }

    fun actualizarDescripcion(nuevaDescripcion: String) {
        if (nuevaDescripcion.length <= 500) {
            _descripcion.value = nuevaDescripcion
            limpiarError()
        }
    }

    fun actualizarCategoria(nuevaCategoria: String) {
        _categoria.value = nuevaCategoria
        limpiarError()
    }

    fun actualizarPrioridad(nuevaPrioridad: String) {
        _prioridad.value = nuevaPrioridad
        limpiarError()
    }

    /**
     * Crear incidencia
     */
    fun crearIncidencia() {
        if (!formularioValido.value) {
            _mensajeError.value = "Por favor, completa todos los campos correctamente"
            return
        }

        _estadoCreacion.value = EstadoCrearIncidencia.Cargando
        _mensajeError.value = null

        viewModelScope.launch {
            try {
                println("DEBUG: === CREANDO INCIDENCIA DESDE VIEWMODEL ===")

                val resultado = incidenciasRepositorio.crearIncidencia(
                    titulo = _titulo.value.trim(),
                    descripcion = _descripcion.value.trim(),
                    categoria = _categoria.value,
                    prioridad = _prioridad.value
                )

                when (resultado) {
                    is ResultadoOperacion.Exito -> {
                        println("DEBUG: ✅ ¡Éxito! Incidencia creada")
                        _estadoCreacion.value = EstadoCrearIncidencia.Exito
                        _mostrarDialogoExito.value = true
                        limpiarFormulario()
                    }
                    is ResultadoOperacion.Error -> {
                        println("DEBUG: ❌ Error: ${resultado.mensaje}")
                        _estadoCreacion.value = EstadoCrearIncidencia.Error(resultado.mensaje)
                        _mensajeError.value = resultado.mensaje
                    }
                    is ResultadoOperacion.Cargando -> {
                        // Ya está en estado de carga
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: ❌ Excepción: ${e.message}")
                _estadoCreacion.value = EstadoCrearIncidencia.Error("Error inesperado")
                _mensajeError.value = "Error inesperado al crear la incidencia"
            }
        }
    }

    /**
     * Funciones de utilidad
     */
    fun obtenerColorPrioridad(prioridad: String): Long {
        return Incidencia.obtenerColorPrioridad(prioridad)
    }

    fun obtenerIconoCategoria(categoria: String): String {
        return Incidencia.obtenerIconoCategoria(categoria)
    }

    fun validarLongitudTitulo(): String? {
        return when {
            _titulo.value.length < 5 -> "Mínimo 5 caracteres"
            _titulo.value.length > 80 -> "Máximo 80 caracteres"
            else -> null
        }
    }

    fun validarLongitudDescripcion(): String? {
        return when {
            _descripcion.value.length < 10 -> "Mínimo 10 caracteres"
            _descripcion.value.length > 500 -> "Máximo 500 caracteres"
            else -> null
        }
    }

    fun obtenerDescripcionPrioridad(prioridad: String): String {
        return when (prioridad.lowercase()) {
            "alta" -> "Requiere atención inmediata"
            "media" -> "Importante pero no urgente"
            "baja" -> "Se puede resolver cuando sea posible"
            else -> ""
        }
    }

    /**
     * Funciones de estado
     */
    private fun limpiarError() {
        if (_mensajeError.value != null) {
            _mensajeError.value = null
        }
        if (_estadoCreacion.value is EstadoCrearIncidencia.Error) {
            _estadoCreacion.value = EstadoCrearIncidencia.Inicial
        }
    }

    fun reiniciarEstado() {
        _estadoCreacion.value = EstadoCrearIncidencia.Inicial
        _mensajeError.value = null
    }

    private fun limpiarFormulario() {
        _titulo.value = ""
        _descripcion.value = ""
        _categoria.value = ""
        _prioridad.value = "media"
    }

    fun limpiarFormularioManual() {
        limpiarFormulario()
        limpiarError()
    }

    fun ocultarDialogoExito() {
        _mostrarDialogoExito.value = false
    }

    /**
     * Funciones para obtener opciones predefinidas
     */
    fun obtenerProblemasComunes(categoria: String): List<String> {
        return when (categoria.lowercase()) {
            "mantenimiento" -> listOf(
                "Reparación general",
                "Pintura necesaria",
                "Revisión preventiva"
            )
            "electricidad" -> listOf(
                "Luz fundida",
                "Cortocircuito",
                "Instalación defectuosa"
            )
            "fontanería" -> listOf(
                "Fuga de agua",
                "Atasco en tubería",
                "Presión baja"
            )
            "limpieza" -> listOf(
                "Zona sucia",
                "Residuos acumulados",
                "Falta mantenimiento"
            )
            "seguridad" -> listOf(
                "Cerradura rota",
                "Cámara averiada",
                "Acceso no controlado"
            )
            "ruidos" -> listOf(
                "Ruido excesivo",
                "Horario inadecuado",
                "Actividad molesta"
            )
            else -> listOf(
                "Problema general",
                "Revisión necesaria",
                "Consulta"
            )
        }
    }
}