package com.example.appcomunidades.pantallas

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcomunidades.ui.theme.*
import com.example.appcomunidades.viewmodels.RegistroViewModel
import com.example.appcomunidades.viewmodels.EstadoRegistro
import kotlinx.coroutines.launch

@Composable
fun FotoPerfil(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(ColorPrimario, ColorPrimarioVariante),
                    radius = 300f
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Añadir foto",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Añadir foto",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDeTextoPersonalizado(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esContrasenna: Boolean = false,
    mensajeError: String? = null,
    esObligatorio: Boolean = false,
    modificador: Modifier = Modifier
) {
    var mostrarContrasenna by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = valor,
            onValueChange = alCambiarValor,
            label = {
                Row {
                    Text(etiqueta)
                    if (esObligatorio) {
                        Text(
                            text = " *",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = icono,
                    contentDescription = etiqueta,
                    tint = ColorPrimario
                )
            },
            trailingIcon = {
                if (esContrasenna) {
                    IconButton(onClick = { mostrarContrasenna = !mostrarContrasenna }) {
                        Text(
                            text = if (mostrarContrasenna) "👁️" else "🙈",
                            fontSize = 16.sp
                        )
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = ColorPrimario,
                focusedBorderColor = ColorPrimario,
                unfocusedBorderColor = ColorPrimarioVariante,
                focusedLabelColor = ColorPrimario,
                unfocusedLabelColor = ColorPrimarioVariante,
                containerColor = Color.White,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = tipoTeclado
            ),
            visualTransformation = if (esContrasenna && !mostrarContrasenna)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            isError = mensajeError != null,
            modifier = modificador
                .fillMaxWidth()
                .padding(vertical = 8.dp)
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

@Composable
fun SelectorAdministrador(
    esAdmin: Boolean,
    alCambiar: (Boolean) -> Unit,
    modificador: Modifier = Modifier
) {
    Card(
        modifier = modificador
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esAdmin) ColorPrimario.copy(alpha = 0.1f) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (esAdmin) ColorPrimario else ColorPrimarioVariante.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (esAdmin) Icons.Default.AccountCircle else Icons.Default.Person,
                        contentDescription = "Rol",
                        tint = if (esAdmin) ColorPrimario else ColorSecundario,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (esAdmin) "Administrador" else "Vecino",
                            color = ColorTexto,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (esAdmin) "Crear nueva comunidad" else "Unirse a comunidad existente",
                            color = ColorSecundario,
                            fontSize = 12.sp
                        )
                    }
                }

                Switch(
                    checked = esAdmin,
                    onCheckedChange = alCambiar,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ColorPrimario,
                        uncheckedThumbColor = ColorFondoSecundario,
                        uncheckedTrackColor = ColorFondoSecundario.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
fun NotaInformativa(
    texto: String,
    modificador: Modifier = Modifier
) {
    Card(
        modifier = modificador.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ColorPrimario.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Información",
                tint = ColorPrimario,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = texto,
                color = ColorSecundario,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun BotonRegistrarse(
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
                    text = "CREANDO CUENTA...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        } else {
            Text(
                text = "CREAR CUENTA",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DialogoExito(
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
                    text = "¡Cuenta creada con éxito!",
                    textAlign = TextAlign.Center,
                    color = ColorTexto
                )
            },
            text = {
                Text(
                    text = "Tu cuenta ha sido registrada correctamente. Ya puedes iniciar sesión en la aplicación.",
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
                    Text("Continuar", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DialogoError(
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
                    text = "Error en el registro",
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
                    Text("Entendido", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(
    onVolverClick: () -> Unit = {},
    onRegistrarseClick: () -> Unit = {},
    viewModel: RegistroViewModel = viewModel()
) {
    // Obtener estados del ViewModel
    val nombre by viewModel.nombre.collectAsState()
    val email by viewModel.email.collectAsState()
    val contrasenna by viewModel.contrasenna.collectAsState()
    val confirmarContrasenna by viewModel.confirmarContrasenna.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val idComunidad by viewModel.idComunidad.collectAsState()
    val esAdministrador by viewModel.esAdministrador.collectAsState()

    val estadoRegistro by viewModel.estadoRegistro.collectAsState()
    val mostrarDialogoExito by viewModel.mostrarDialogoExito.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val formularioEsValido by viewModel.formularioValido.collectAsState()

    // Estados locales para UI
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
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
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                FotoPerfil(onClick = {
                    //No aprobada para la demo
                })

                Spacer(modifier = Modifier.height(24.dp))

                // Formulario
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
                            text = "Información Personal",
                            color = ColorTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            textAlign = TextAlign.Center
                        )

                        CampoDeTextoPersonalizado(
                            valor = nombre,
                            alCambiarValor = viewModel::actualizarNombre,
                            etiqueta = "Nombre completo",
                            icono = Icons.Default.Person,
                            esObligatorio = true
                        )

                        CampoDeTextoPersonalizado(
                            valor = email,
                            alCambiarValor = viewModel::actualizarEmail,
                            etiqueta = "Email",
                            icono = Icons.Default.Email,
                            tipoTeclado = KeyboardType.Email,
                            esObligatorio = true
                        )

                        CampoDeTextoPersonalizado(
                            valor = contrasenna,
                            alCambiarValor = viewModel::actualizarContrasenna,
                            etiqueta = "Contraseña",
                            icono = Icons.Default.Lock,
                            tipoTeclado = KeyboardType.Password,
                            esContrasenna = true,
                            esObligatorio = true
                        )

                        CampoDeTextoPersonalizado(
                            valor = confirmarContrasenna,
                            alCambiarValor = viewModel::actualizarConfirmarContrasenna,
                            etiqueta = "Confirmar contraseña",
                            icono = Icons.Default.Lock,
                            tipoTeclado = KeyboardType.Password,
                            esContrasenna = true,
                            esObligatorio = true,
                            mensajeError = viewModel.obtenerMensajeErrorContrasenna()
                        )

                        CampoDeTextoPersonalizado(
                            valor = telefono,
                            alCambiarValor = viewModel::actualizarTelefono,
                            etiqueta = "Teléfono",
                            icono = Icons.Default.Phone,
                            tipoTeclado = KeyboardType.Phone,
                            esObligatorio = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tipo de Cuenta",
                            color = ColorTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        SelectorAdministrador(
                            esAdmin = esAdministrador,
                            alCambiar = viewModel::actualizarEsAdministrador
                        )

                        AnimatedVisibility(
                            visible = !esAdministrador,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))

                                CampoDeTextoPersonalizado(
                                    valor = idComunidad,
                                    alCambiarValor = viewModel::actualizarIdComunidad,
                                    etiqueta = "ID de Comunidad",
                                    icono = Icons.Outlined.Home,
                                    esObligatorio = true,
                                    mensajeError = if (!esAdministrador && idComunidad.isEmpty() && estadoRegistro is EstadoRegistro.Error)
                                        "El ID de comunidad es obligatorio para vecinos"
                                    else null
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                NotaInformativa(
                                    texto = "Solicita el ID de comunidad a tu administrador. Formato: Cs0000001a"
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = esAdministrador,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                NotaInformativa(
                                    texto = "Como administrador, crearás una nueva comunidad al registrarte. Se generará un ID único automáticamente."
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                BotonRegistrarse(
                    habilitado = formularioEsValido,
                    cargando = estadoRegistro is EstadoRegistro.Cargando,
                    onClick = {
                        scope.launch {
                            viewModel.registrarUsuario()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Diálogos
    DialogoExito(
        mostrar = mostrarDialogoExito,
        onDismiss = {
            viewModel.ocultarDialogoExito()
            viewModel.reiniciarEstado()
        },
        onContinuar = {
            viewModel.ocultarDialogoExito()
            viewModel.reiniciarEstado()
            onRegistrarseClick()
        }
    )

    DialogoError(
        mostrar = mensajeError != null,
        mensaje = mensajeError ?: "",
        onDismiss = viewModel::reiniciarEstado
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaRegistroPreview() {
    TemaAppComunidades {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ColorFondo
        ) {
            PantallaRegistroSinViewModel()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PantallaRegistroSinViewModel() {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasenna by remember { mutableStateOf("") }
    var confirmarContrasenna by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var idComunidad by remember { mutableStateOf("") }
    var esAdministrador by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
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
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                FotoPerfil(onClick = { })

                Spacer(modifier = Modifier.height(24.dp))

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
                            text = "Información Personal",
                            color = ColorTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            textAlign = TextAlign.Center
                        )

                        CampoDeTextoPersonalizado(
                            valor = nombre,
                            alCambiarValor = { nombre = it },
                            etiqueta = "Nombre completo",
                            icono = Icons.Default.Person,
                            esObligatorio = true
                        )

                        CampoDeTextoPersonalizado(
                            valor = email,
                            alCambiarValor = { email = it },
                            etiqueta = "Email",
                            icono = Icons.Default.Email,
                            tipoTeclado = KeyboardType.Email,
                            esObligatorio = true
                        )

                        CampoDeTextoPersonalizado(
                            valor = contrasenna,
                            alCambiarValor = { contrasenna = it },
                            etiqueta = "Contraseña",
                            icono = Icons.Default.Lock,
                            tipoTeclado = KeyboardType.Password,
                            esContrasenna = true,
                            esObligatorio = true
                        )

                        CampoDeTextoPersonalizado(
                            valor = confirmarContrasenna,
                            alCambiarValor = { confirmarContrasenna = it },
                            etiqueta = "Confirmar contraseña",
                            icono = Icons.Default.Lock,
                            tipoTeclado = KeyboardType.Password,
                            esContrasenna = true,
                            esObligatorio = true
                        )

                        CampoDeTextoPersonalizado(
                            valor = telefono,
                            alCambiarValor = { telefono = it },
                            etiqueta = "Teléfono",
                            icono = Icons.Default.Phone,
                            tipoTeclado = KeyboardType.Phone,
                            esObligatorio = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tipo de Cuenta",
                            color = ColorTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        SelectorAdministrador(
                            esAdmin = esAdministrador,
                            alCambiar = { esAdministrador = it }
                        )

                        AnimatedVisibility(
                            visible = !esAdministrador,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))

                                CampoDeTextoPersonalizado(
                                    valor = idComunidad,
                                    alCambiarValor = { idComunidad = it },
                                    etiqueta = "ID de Comunidad",
                                    icono = Icons.Outlined.Home,
                                    esObligatorio = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                NotaInformativa(
                                    texto = "Solicita el ID de comunidad a tu administrador. Formato: Cs0000001a"
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = esAdministrador,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                NotaInformativa(
                                    texto = "Como administrador, crearás una nueva comunidad al registrarte. Se generará un ID único automáticamente."
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                BotonRegistrarse(
                    habilitado = true,
                    cargando = false,
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}