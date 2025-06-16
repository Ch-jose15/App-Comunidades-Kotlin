package com.example.appcomunidades.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcomunidades.ui.theme.*
import com.example.appcomunidades.viewmodels.RecuperarContrasennaViewModel
import com.example.appcomunidades.viewmodels.EstadoRecuperacion
import kotlinx.coroutines.launch

/* COMPONENTES ESPECÍFICOS PARA RECUPERAR CONTRASEÑA */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoEmailRecuperacion(
    email: String,
    onEmailChange: (String) -> Unit,
    esValido: Boolean,
    mostrarError: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo electrónico") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = ColorPrimario
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = ColorPrimario,
                focusedBorderColor = if (mostrarError && !esValido)
                    MaterialTheme.colorScheme.error else ColorPrimario,
                unfocusedBorderColor = if (mostrarError && !esValido)
                    MaterialTheme.colorScheme.error else ColorPrimarioVariante,
                focusedLabelColor = if (mostrarError && !esValido)
                    MaterialTheme.colorScheme.error else ColorPrimario,
                unfocusedLabelColor = ColorPrimarioVariante,
                containerColor = Color.White,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            isError = mostrarError && !esValido,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        if (mostrarError && !esValido) {
            Text(
                text = "Por favor, introduce un email válido",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun BotonEnviarRecuperacion(
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
                    text = "ENVIANDO...",
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
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Enviar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ENVIAR ENLACE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun TarjetaInformativa(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ColorPrimario.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Información",
                tint = ColorPrimario,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "¿Cómo funciona?",
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Te enviaremos un enlace a tu correo electrónico para que puedas crear una nueva contraseña de forma segura.",
                    color = ColorSecundario,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun DialogoExitoRecuperacion(
    mostrar: Boolean,
    email: String,
    onDismiss: () -> Unit,
    onVolverLogin: () -> Unit
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
                    text = "¡Enlace enviado!",
                    textAlign = TextAlign.Center,
                    color = ColorTexto,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Hemos enviado un enlace de recuperación a:",
                        textAlign = TextAlign.Center,
                        color = ColorSecundario,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = email,
                        textAlign = TextAlign.Center,
                        color = ColorPrimario,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Revisa tu bandeja de entrada y sigue las instrucciones del correo.",
                        textAlign = TextAlign.Center,
                        color = ColorSecundario,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        onVolverLogin()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimario
                    )
                ) {
                    Text("Volver al inicio", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Entendido", color = ColorPrimario)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DialogoErrorRecuperacion(
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
                    text = "Error en la recuperación",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
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

/* PANTALLA PRINCIPAL DE RECUPERAR CONTRASEÑA */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRecuperarContrasenna(
    onVolverClick: () -> Unit = {},
    onVolverLoginClick: () -> Unit = {},
    viewModel: RecuperarContrasennaViewModel = viewModel()
) {
    // Estados del ViewModel
    val email by viewModel.email.collectAsState()
    val estadoRecuperacion by viewModel.estadoRecuperacion.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val emailEsValido by viewModel.emailEsValido.collectAsState()
    val mostrarDialogoExito by viewModel.mostrarDialogoExito.collectAsState()

    // Estados locales
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var mostrarError by remember { mutableStateOf(false) }

    // Manejar navegación después del éxito
    LaunchedEffect(estadoRecuperacion) {
        if (estadoRecuperacion is EstadoRecuperacion.Exito) {
            // El diálogo se muestra automáticamente desde el ViewModel
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar Contraseña") },
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
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Título principal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTexto,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No te preocupes, te ayudamos a recuperarla",
                        fontSize = 16.sp,
                        color = ColorSecundario,
                        textAlign = TextAlign.Center
                    )
                }

                // Tarjeta informativa
                TarjetaInformativa(
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Formulario principal
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Introduce tu email",
                            color = ColorTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Escribe la dirección de correo electrónico asociada a tu cuenta:",
                            color = ColorSecundario,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        // Campo de email
                        CampoEmailRecuperacion(
                            email = email,
                            onEmailChange = {
                                viewModel.actualizarEmail(it)
                                mostrarError = false
                            },
                            esValido = emailEsValido,
                            mostrarError = mostrarError
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón enviar
                        BotonEnviarRecuperacion(
                            habilitado = emailEsValido,
                            cargando = estadoRecuperacion is EstadoRecuperacion.Cargando,
                            onClick = {
                                mostrarError = !emailEsValido
                                if (emailEsValido) {
                                    scope.launch {
                                        viewModel.enviarRecuperacion()
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Enlace para volver al login
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "¿Ya recordaste tu contraseña?",
                            color = ColorSecundario,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = onVolverLoginClick,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = ColorPrimario
                            )
                        ) {
                            Text(
                                text = "Volver al inicio de sesión",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Diálogos
    DialogoExitoRecuperacion(
        mostrar = mostrarDialogoExito,
        email = email,
        onDismiss = viewModel::ocultarDialogoExito,
        onVolverLogin = {
            viewModel.ocultarDialogoExito()
            viewModel.reiniciarEstado()
            onVolverLoginClick()
        }
    )

    DialogoErrorRecuperacion(
        mostrar = mensajeError != null,
        mensaje = mensajeError ?: "",
        onDismiss = viewModel::reiniciarEstado
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaRecuperarContrasennaPreview() {
    TemaAppComunidades {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ColorFondo
        ) {
            PantallaRecuperarContrasennaSinViewModel()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PantallaRecuperarContrasennaSinViewModel() {
    var email by remember { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar Contraseña") },
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
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTexto,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No te preocupes, te ayudamos a recuperarla",
                        fontSize = 16.sp,
                        color = ColorSecundario,
                        textAlign = TextAlign.Center
                    )
                }

                TarjetaInformativa(
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Introduce tu email",
                            color = ColorTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Escribe la dirección de correo electrónico asociada a tu cuenta:",
                            color = ColorSecundario,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        CampoEmailRecuperacion(
                            email = email,
                            onEmailChange = {
                                email = it
                                mostrarError = false
                            },
                            esValido = email.contains("@") && email.contains("."),
                            mostrarError = mostrarError
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        BotonEnviarRecuperacion(
                            habilitado = email.contains("@") && email.contains("."),
                            cargando = false,
                            onClick = {
                                mostrarError = !(email.contains("@") && email.contains("."))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "¿Ya recordaste tu contraseña?",
                            color = ColorSecundario,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = ColorPrimario
                            )
                        ) {
                            Text(
                                text = "Volver al inicio de sesión",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}