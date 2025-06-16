package com.example.appcomunidades.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcomunidades.ui.theme.*
import com.example.appcomunidades.viewmodels.CrearIncidenciaViewModel
import com.example.appcomunidades.viewmodels.EstadoCrearIncidencia
import com.example.appcomunidades.modelos.Incidencia
import kotlinx.coroutines.launch

/* COMPONENTES ESPECÍFICOS PARA INCIDENCIAS */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoIncidencia(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    lineasMaximas: Int = 1,
    mensajeError: String? = null,
    textoAyuda: String? = null,
    contadorCaracteres: String? = null,
    modificador: Modifier = Modifier
) {
    Column {
        OutlinedTextField(
            value = valor,
            onValueChange = alCambiarValor,
            label = { Text(etiqueta) },
            leadingIcon = {
                Icon(
                    imageVector = icono,
                    contentDescription = etiqueta,
                    tint = Color(0xFF253745) // ColorPrimarioVariante
                )
            },
            trailingIcon = {
                if (contadorCaracteres != null) {
                    Text(
                        text = contadorCaracteres,
                        fontSize = 12.sp,
                        color = Color(0xFF9BA8AB) // ColorSecundario
                    )
                }
            },
            maxLines = lineasMaximas,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color(0xFF253745),
                focusedBorderColor = Color(0xFF253745),
                unfocusedBorderColor = Color(0xFF9BA8AB),
                focusedLabelColor = Color(0xFF253745),
                unfocusedLabelColor = Color(0xFF9BA8AB),
                containerColor = Color.White,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = tipoTeclado
            ),
            isError = mensajeError != null,
            modifier = modificador
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Texto de ayuda
        if (textoAyuda != null && mensajeError == null) {
            Text(
                text = textoAyuda,
                color = Color(0xFF9BA8AB),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // Mensaje de error
        if (mensajeError != null) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorCategoria(
    categoriaSeleccionada: String,
    categorias: List<String>,
    alSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = !expandido }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = { },
                readOnly = true,
                label = { Text("Categoría") },
                leadingIcon = {
                    Text(
                        text = if (categoriaSeleccionada.isNotEmpty())
                            Incidencia.obtenerIconoCategoria(categoriaSeleccionada)
                        else "📋",
                        fontSize = 20.sp
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color(0xFF253745),
                    focusedBorderColor = Color(0xFF253745),
                    unfocusedBorderColor = Color(0xFF9BA8AB),
                    focusedLabelColor = Color(0xFF253745),
                    unfocusedLabelColor = Color(0xFF9BA8AB),
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .padding(vertical = 8.dp)
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                modifier = Modifier.background(Color.White)
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Incidencia.obtenerIconoCategoria(categoria),
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = categoria,
                                    color = Color(0xFF4A5C6A)
                                )
                            }
                        },
                        onClick = {
                            alSeleccionar(categoria)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectorPrioridad(
    prioridadSeleccionada: String,
    prioridades: List<String>,
    alSeleccionar: (String) -> Unit,
    obtenerDescripcion: (String) -> String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Prioridad",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4A5C6A),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            prioridades.forEach { prioridad ->
                val estaSeleccionado = prioridadSeleccionada == prioridad
                val colorPrioridad = Color(Incidencia.obtenerColorPrioridad(prioridad))

                Card(
                    onClick = { alSeleccionar(prioridad) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (estaSeleccionado)
                            colorPrioridad.copy(alpha = 0.1f)
                        else
                            Color.White
                    ),
                    border = if (estaSeleccionado)
                        androidx.compose.foundation.BorderStroke(2.dp, colorPrioridad)
                    else
                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCD0CF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (prioridad) {
                                "alta" -> "🔴"
                                "media" -> "🟡"
                                "baja" -> "🟢"
                                else -> "⚪"
                            },
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prioridad.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Normal,
                            color = if (estaSeleccionado) colorPrioridad else Color(0xFF9BA8AB)
                        )
                    }
                }
            }
        }

        // Descripción de la prioridad seleccionada
        if (prioridadSeleccionada.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = obtenerDescripcion(prioridadSeleccionada),
                fontSize = 12.sp,
                color = Color(0xFF9BA8AB),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BotonCrearIncidencia(
    habilitado: Boolean,
    cargando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = habilitado && !cargando,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF253745),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF9BA8AB).copy(alpha = 0.3f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (cargando) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CREANDO...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Crear",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CREAR INCIDENCIA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun DialogoExitoIncidencia(
    mostrar: Boolean,
    onDismiss: () -> Unit,
    onContinuar: () -> Unit
) {
    if (mostrar) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    tint = Color(0xFF253745),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "¡Incidencia creada con éxito!",
                    textAlign = TextAlign.Center,
                    color = Color(0xFF4A5C6A)
                )
            },
            text = {
                Text(
                    text = "Tu incidencia ha sido registrada correctamente. Los administradores recibirán una notificación.",
                    textAlign = TextAlign.Center,
                    color = Color(0xFF9BA8AB)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        onContinuar()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF253745)
                    )
                ) {
                    Text("Continuar", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DialogoErrorIncidencia(
    mostrar: Boolean,
    mensaje: String,
    onDismiss: () -> Unit
) {
    if (mostrar) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Error al crear incidencia",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = mensaje,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF9BA8AB)
                )
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Entendido", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/* PANTALLA PRINCIPAL PARA CREAR INCIDENCIAS */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearIncidencia(
    onVolverClick: () -> Unit = {},
    onIncidenciaCreada: () -> Unit = {},
    viewModel: CrearIncidenciaViewModel = viewModel()
) {
    // Estados del ViewModel
    val titulo by viewModel.titulo.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val categoria by viewModel.categoria.collectAsState()
    val prioridad by viewModel.prioridad.collectAsState()

    val estadoCreacion by viewModel.estadoCreacion.collectAsState()
    val mostrarDialogoExito by viewModel.mostrarDialogoExito.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val formularioValido by viewModel.formularioValido.collectAsState()

    val contadorTitulo by viewModel.contadorTitulo.collectAsState()
    val contadorDescripcion by viewModel.contadorDescripcion.collectAsState()

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Incidencia") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Botón de limpiar formulario
                    TextButton(
                        onClick = { viewModel.limpiarFormularioManual() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Limpiar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF253745),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFCCD0CF)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFCCD0CF),
                            Color(0xFF9BA8AB).copy(alpha = 0.5f)
                        ),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabecera informativa
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Información",
                            tint = Color(0xFF253745),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Describe el problema que has detectado en tu comunidad para que pueda ser resuelto.",
                            fontSize = 14.sp,
                            color = Color(0xFF4A5C6A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Formulario principal
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Detalles de la Incidencia",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A5C6A),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        // Campo título
                        CampoTextoIncidencia(
                            valor = titulo,
                            alCambiarValor = viewModel::actualizarTitulo,
                            etiqueta = "Título del problema",
                            icono = Icons.Default.Edit,
                            contadorCaracteres = contadorTitulo,
                            mensajeError = viewModel.validarLongitudTitulo(),
                            textoAyuda = "Describe brevemente el problema"
                        )

                        // Campo descripción
                        CampoTextoIncidencia(
                            valor = descripcion,
                            alCambiarValor = viewModel::actualizarDescripcion,
                            etiqueta = "Descripción detallada",
                            icono = Icons.Default.Edit,
                            tipoTeclado = KeyboardType.Text,
                            lineasMaximas = 4,
                            contadorCaracteres = contadorDescripcion,
                            mensajeError = viewModel.validarLongitudDescripcion(),
                            textoAyuda = "Explica el problema con más detalle"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Selector de categoría
                        SelectorCategoria(
                            categoriaSeleccionada = categoria,
                            categorias = viewModel.categoriasDisponibles,
                            alSeleccionar = viewModel::actualizarCategoria
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Selector de prioridad
                        SelectorPrioridad(
                            prioridadSeleccionada = prioridad,
                            prioridades = viewModel.prioridadesDisponibles,
                            alSeleccionar = viewModel::actualizarPrioridad,
                            obtenerDescripcion = viewModel::obtenerDescripcionPrioridad
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón crear incidencia
                BotonCrearIncidencia(
                    habilitado = formularioValido,
                    cargando = estadoCreacion is EstadoCrearIncidencia.Cargando,
                    onClick = {
                        scope.launch {
                            viewModel.crearIncidencia()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Diálogos
    DialogoExitoIncidencia(
        mostrar = mostrarDialogoExito,
        onDismiss = {
            viewModel.ocultarDialogoExito()
            viewModel.reiniciarEstado()
        },
        onContinuar = {
            viewModel.ocultarDialogoExito()
            viewModel.reiniciarEstado()
            onIncidenciaCreada()
        }
    )

    DialogoErrorIncidencia(
        mostrar = mensajeError != null,
        mensaje = mensajeError ?: "",
        onDismiss = viewModel::reiniciarEstado
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaCrearIncidenciaPreview() {
    TemaAppComunidades {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFCCD0CF)
        ) {
            PantallaCrearIncidenciaPreview_SinViewModel()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PantallaCrearIncidenciaPreview_SinViewModel() {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("media") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Incidencia") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF253745),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFCCD0CF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            BotonCrearIncidencia(
                habilitado = true,
                cargando = false,
                onClick = { }
            )
        }
    }
}