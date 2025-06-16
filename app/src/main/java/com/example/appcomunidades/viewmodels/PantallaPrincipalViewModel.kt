package com.example.appcomunidades.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcomunidades.repositorios.AnunciosRepositorio
import com.example.appcomunidades.repositorios.IncidenciasRepositorio
import com.example.appcomunidades.repositorios.ResultadoOperacion
import com.example.appcomunidades.modelos.Anuncio
import com.example.appcomunidades.modelos.Incidencia
import com.example.appcomunidades.pantallas.AnuncioEjemplo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal
 * Maneja tanto anuncios como incidencias
 */
class PantallaPrincipalViewModel : ViewModel() {

    private val anunciosRepositorio = AnunciosRepositorio()
    private val incidenciasRepositorio = IncidenciasRepositorio()

    // === ESTADOS PARA ANUNCIOS ===
    private val _anuncios = MutableStateFlow<List<Anuncio>>(emptyList())
    val anuncios: StateFlow<List<Anuncio>> = _anuncios.asStateFlow()

    private val _estaCargandoAnuncios = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargandoAnuncios.asStateFlow()

    private val _mensajeErrorAnuncios = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeErrorAnuncios.asStateFlow()

    // Conversión para UI (mantener compatibilidad)
    val anunciosParaUI: StateFlow<List<AnuncioEjemplo>> = _anuncios.map { anuncios ->
        anuncios.map { anuncio ->
            AnuncioEjemplo(
                id = anuncio.id,
                titulo = anuncio.titulo,
                contenido = anuncio.contenido,
                esUrgente = anuncio.es_urgente,
                fecha = anuncio.fecha_formateada,
                autor = anuncio.nombre_autor,
                categoria = anuncio.categoria,
                colorCategoria = obtenerColorCategoria(anuncio.categoria),
                comunidadId = anuncio.comunidad_id
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // === ESTADOS PARA INCIDENCIAS ===
    private val _incidencias = MutableStateFlow<List<Incidencia>>(emptyList())
    val incidencias: StateFlow<List<Incidencia>> = _incidencias.asStateFlow()

    private val _estaCargandoIncidencias = MutableStateFlow(false)
    val estaCargandoIncidencias: StateFlow<Boolean> = _estaCargandoIncidencias.asStateFlow()

    private val _mensajeErrorIncidencias = MutableStateFlow<String?>(null)
    val mensajeErrorIncidencias: StateFlow<String?> = _mensajeErrorIncidencias.asStateFlow()

    init {
        // Cargar ambos al inicio
        refrescarAnuncios()
        refrescarIncidencias()
    }

    /**
     * === FUNCIONES PARA ANUNCIOS ===
     */
    fun refrescarAnuncios() {
        viewModelScope.launch {
            _estaCargandoAnuncios.value = true
            _mensajeErrorAnuncios.value = null

            try {
                val resultado = anunciosRepositorio.obtenerTodosLosAnuncios()
                when (resultado) {
                    is ResultadoOperacion.Exito -> {
                        _anuncios.value = resultado.datos
                        _estaCargandoAnuncios.value = false
                    }
                    is ResultadoOperacion.Error -> {
                        _mensajeErrorAnuncios.value = resultado.mensaje
                        _estaCargandoAnuncios.value = false
                    }
                    is ResultadoOperacion.Cargando -> {
                        // Mantener estado de carga
                    }
                }
            } catch (e: Exception) {
                _mensajeErrorAnuncios.value = "Error al cargar anuncios"
                _estaCargandoAnuncios.value = false
            }
        }
    }

    fun limpiarError() {
        _mensajeErrorAnuncios.value = null
    }

    /**
     * === FUNCIONES PARA INCIDENCIAS ===
     */
    fun refrescarIncidencias() {
        viewModelScope.launch {
            _estaCargandoIncidencias.value = true
            _mensajeErrorIncidencias.value = null

            try {
                val resultado = incidenciasRepositorio.obtenerTodasLasIncidencias()
                when (resultado) {
                    is ResultadoOperacion.Exito -> {
                        _incidencias.value = resultado.datos
                        _estaCargandoIncidencias.value = false
                    }
                    is ResultadoOperacion.Error -> {
                        _mensajeErrorIncidencias.value = resultado.mensaje
                        _estaCargandoIncidencias.value = false
                    }
                    is ResultadoOperacion.Cargando -> {
                        // Mantener estado de carga
                    }
                }
            } catch (e: Exception) {
                _mensajeErrorIncidencias.value = "Error al cargar incidencias"
                _estaCargandoIncidencias.value = false
            }
        }
    }

    fun limpiarErrorIncidencias() {
        _mensajeErrorIncidencias.value = null
    }

    /**
     * === FUNCIONES AUXILIARES ===
     */
    private fun obtenerColorCategoria(categoria: String): Long {
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
}