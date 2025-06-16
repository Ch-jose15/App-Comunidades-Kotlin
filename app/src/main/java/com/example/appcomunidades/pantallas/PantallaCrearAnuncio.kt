package com.example.appcomunidades.pantallas

import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcomunidades.ui.theme.*
import com.example.appcomunidades.viewmodels.CrearAnuncioViewModel
import com.example.appcomunidades.viewmodels.EstadoCrearAnuncio
import kotlinx.coroutines.launch

@Composable
fun CampoTextoAnuncio(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    placeholder: String = "",
    lineasMaximas: Int = 1,
    contadorCaracteres: String? = null,
    mensajeError: String? = null,
    modificador: Modifier = Modifier
) {
    Column(modifier = modificador) {
        OutlinedTextField(
            value = valor,
            onValueChange = alCambiarValor,
            label = { Text(etiqueta) },
            placeholder = { Text(placeholder, color = ColorSecundario.copy(alpha = 0.7f)) },
            leadingIcon = {
                Icon(
                    imageVector = icono,
                    contentDescription = etiqueta,
                    tint = ColorPrimario
                )
            },
            trailingIcon = {
                if (contadorCaracteres != null) {
                    Text(
                        text = contadorCaracteres,
                        fontSize = 12.sp,
                        color = ColorSecundario,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            },
            maxLines = lineasMaximas,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorPrimario,
                unfocusedBorderColor = ColorPrimarioVariante,
                focusedLabelColor = ColorPrimario,
                unfocusedLabelColor = ColorPrimarioVariante
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            ),
            isError = mensajeError != null,
            modifier = Modifier.fillMaxWidth()
        )

        // Mostrar mensaje de error si existe
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
    categoriasDisponibles: List<String>,
    alSeleccionarCategoria: (String) -> Unit,
    obtenerColorCategoria: (String) -> Long,
    modificador: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Column(modifier = modificador) {
        // Título de la sección
        Text(
            text = "Categoría del anuncio",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = ColorTexto,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Dropdown personalizado
        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = !expandido }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada.ifEmpty { "Selecciona una categoría" },
                onValueChange = { },
                readOnly = true,
                label = { Text("Categoría") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Categoría",
                        tint = if (categoriaSeleccionada.isNotEmpty()) {
                            Color(obtenerColorCategoria(categoriaSeleccionada))
                        } else {
                            ColorPrimario
                        }
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorPrimario,
                    unfocusedBorderColor = ColorPrimarioVariante,
                    focusedLabelColor = ColorPrimario
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                modifier = Modifier.background(Color.White)
            ) {
                categoriasDisponibles.forEach { categoria ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(obtenerColorCategoria(categoria)))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(categoria)
                            }
                        },
                        onClick = {
                            alSeleccionarCategoria(categoria)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectorUrgencia(
    esUrgente: Boolean,
    alCambiar: (Boolean) -> Unit,
    modificador: Modifier = Modifier
) {
    Card(
        modifier = modificador.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esUrgente) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
            } else {
                Color.White
            }
        ),
        border = if (esUrgente) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.error)
        } else {
            BorderStroke(1.dp, ColorPrimarioVariante.copy(alpha = 0.3f))
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Urgente",
                    tint = if (esUrgente) MaterialTheme.colorScheme.error else ColorSecundario,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Anuncio urgente",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (esUrgente) MaterialTheme.colorScheme.error else ColorTexto
                    )
                    Text(
                        text = "Se mostrará destacado y con prioridad",
                        fontSize = 12.sp,
                        color = ColorSecundario
                    )
                }
            }

            Switch(
                checked = esUrgente,
                onCheckedChange = alCambiar,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.error,
                    uncheckedThumbColor = ColorFondoSecundario,
                    uncheckedTrackColor = ColorFondoSecundario.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun BotonCrearAnuncio(
    habilitado: Boolean,
    cargando: Boolean,
    onClick: () -> Unit,
    modificador: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = habilitado && !cargando,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPrimario,
            contentColor = Color.White,
            disabledContainerColor = ColorFondoSecundario.copy(alpha = 0.3f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        ),
        modifier = modificador
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
                    imageVector = Icons.Default.Send,
                    contentDescription = "Crear",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PUBLICAR ANUNCIO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun DialogoExitoAnuncio(
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
                    tint = ColorPrimario,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "¡Anuncio publicado!",
                    textAlign = TextAlign.Center,
                    color = ColorTexto
                )
            },
            text = {
                Text(
                    text = "Tu anuncio ha sido publicado exitosamente y ya es visible para toda la comunidad.",
                    textAlign = TextAlign.Center,
                    color = ColorSecundario
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        onContinuar()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimario
                    )
                ) {
                    Text("Ver anuncios", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cerrar", color = ColorSecundario)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DialogoErrorAnuncio(
    mostrar: Boolean,
    mensaje: String,
    onDismiss: () -> Unit
) {
    if (mostrar) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Error al publicar",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = mensaje,
                    textAlign = TextAlign.Center,
                    color = ColorSecundario
                )
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reintentar", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/* PANTALLA PRINCIPAL PARA CREAR ANUNCIO */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearAnuncio(
    onVolverClick: () -> Unit = {},
    onAnuncioCreado: () -> Unit = {},
    viewModel: CrearAnuncioViewModel = viewModel()
) {
    // Obtener estados del ViewModel
    val titulo by viewModel.titulo.collectAsState()
    val contenido by viewModel.contenido.collectAsState()
    val categoria by viewModel.categoria.collectAsState()
    val esUrgente by viewModel.esUrgente.collectAsState()

    val estadoCreacion by viewModel.estadoCreacion.collectAsState()
    val formularioValido by viewModel.formularioValido.collectAsState()
    val mostrarDialogoExito by viewModel.mostrarDialogoExito.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    val contadorTitulo by viewModel.contadorTitulo.collectAsState()
    val contadorContenido by viewModel.contadorContenido.collectAsState()

    // Estados locales para UI
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Manejar navegación después del éxito
    LaunchedEffect(estadoCreacion) {
        if (estadoCreacion is EstadoCrearAnuncio.Exito) {
            // Auto-navegación después de mostrar el diálogo
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Anuncio") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Botón para limpiar formulario
                    IconButton(
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
                    containerColor = ColorPrimario,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = ColorFondo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ColorFondo, ColorFondoSecundario.copy(alpha = 0.5f)),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Encabezado informativo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ColorPrimario.copy(alpha = 0.1f)
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
                            tint = ColorPrimario,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Crea un anuncio para comunicarte con toda la comunidad",
                            fontSize = 14.sp,
                            color = ColorTexto,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Formulario principal
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Información del Anuncio",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTexto,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        // Campo Título
                        CampoTextoAnuncio(
                            valor = titulo,
                            alCambiarValor = viewModel::actualizarTitulo,
                            etiqueta = "Título del anuncio",
                            icono = Icons.Default.Star,
                            placeholder = "Escribe un título claro y descriptivo",
                            contadorCaracteres = contadorTitulo,
                            mensajeError = viewModel.validarLongitudTitulo(),
                            modificador = Modifier.padding(bottom = 16.dp)
                        )

                        // Campo Contenido
                        CampoTextoAnuncio(
                            valor = contenido,
                            alCambiarValor = viewModel::actualizarContenido,
                            etiqueta = "Contenido del anuncio",
                            icono = Icons.Default.Menu,
                            placeholder = "Describe detalladamente la información que quieres comunicar",
                            lineasMaximas = 8,
                            contadorCaracteres = contadorContenido,
                            mensajeError = viewModel.validarLongitudContenido(),
                            modificador = Modifier.padding(bottom = 20.dp)
                        )

                        // Selector de Categoría
                        SelectorCategoria(
                            categoriaSeleccionada = categoria,
                            categoriasDisponibles = viewModel.categoriasDisponibles,
                            alSeleccionarCategoria = viewModel::actualizarCategoria,
                            obtenerColorCategoria = viewModel::obtenerColorCategoria,
                            modificador = Modifier.padding(bottom = 20.dp)
                        )

                        // Selector de Urgencia
                        SelectorUrgencia(
                            esUrgente = esUrgente,
                            alCambiar = viewModel::actualizarEsUrgente,
                            modificador = Modifier.padding(bottom = 24.dp)
                        )

                        // Botón para crear anuncio
                        BotonCrearAnuncio(
                            habilitado = formularioValido,
                            cargando = estadoCreacion is EstadoCrearAnuncio.Cargando,
                            onClick = {
                                scope.launch {
                                    viewModel.crearAnuncio()
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Diálogos
    DialogoExitoAnuncio(
        mostrar = mostrarDialogoExito,
        onDismiss = {
            viewModel.ocultarDialogoExito()
            viewModel.reiniciarEstado()
        },
        onContinuar = {
            viewModel.ocultarDialogoExito()
            viewModel.reiniciarEstado()
            onAnuncioCreado()
        }
    )

    DialogoErrorAnuncio(
        mostrar = mensajeError != null,
        mensaje = mensajeError ?: "",
        onDismiss = viewModel::reiniciarEstado
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaCrearAnuncioPreview() {
    TemaAppComunidades {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ColorFondo
        ) {
            PantallaCrearAnuncioSinViewModel()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PantallaCrearAnuncioSinViewModel() {
    // Estados locales para el preview
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var esUrgente by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Anuncio") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorPrimario,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = ColorFondo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ColorFondo, ColorFondoSecundario.copy(alpha = 0.5f)),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
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
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Información del Anuncio",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTexto,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        CampoTextoAnuncio(
                            valor = titulo,
                            alCambiarValor = { titulo = it },
                            etiqueta = "Título del anuncio",
                            icono = Icons.Default.Star,
                            placeholder = "Escribe un título claro y descriptivo",
                            contadorCaracteres = "${titulo.length}/100",
                            modificador = Modifier.padding(bottom = 16.dp)
                        )

                        CampoTextoAnuncio(
                            valor = contenido,
                            alCambiarValor = { contenido = it },
                            etiqueta = "Contenido del anuncio",
                            icono = Icons.Default.Menu,
                            placeholder = "Describe detalladamente la información",
                            lineasMaximas = 8,
                            contadorCaracteres = "${contenido.length}/1000",
                            modificador = Modifier.padding(bottom = 20.dp)
                        )

                        SelectorUrgencia(
                            esUrgente = esUrgente,
                            alCambiar = { esUrgente = it },
                            modificador = Modifier.padding(bottom = 24.dp)
                        )

                        BotonCrearAnuncio(
                            habilitado = true,
                            cargando = false,
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}