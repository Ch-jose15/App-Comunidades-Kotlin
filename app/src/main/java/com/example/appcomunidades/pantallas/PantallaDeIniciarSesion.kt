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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcomunidades.ui.theme.*
import com.example.appcomunidades.viewmodels.InicioSesionViewModel
import com.example.appcomunidades.viewmodels.EstadoInicioSesion
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoInicioSesion(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esContrasenna: Boolean = false,
    modificador: Modifier = Modifier
) {
    var mostrarContrasenna by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = alCambiarValor,
        label = { Text(etiqueta) },
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
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = tipoTeclado
        ),
        visualTransformation = if (esContrasenna && !mostrarContrasenna)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        modifier = modificador
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun BotonIniciarSesion(
    habilitado: Boolean,
    cargando: Boolean = false,
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
                    text = "INICIANDO...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        } else {
            Text(
                text = "INICIAR SESIÓN",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CheckboxRecordarme(
    recordarme: Boolean,
    alCambiar: (Boolean) -> Unit,
    modificador: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modificador
    ) {
        Checkbox(
            checked = recordarme,
            onCheckedChange = alCambiar,
            colors = CheckboxDefaults.colors(
                checkedColor = ColorPrimario,
                uncheckedColor = ColorPrimarioVariante,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Recordar credenciales",
            color = ColorTexto,
            fontSize = 14.sp
        )
    }
}

@Composable
fun TituloHabitatDigital(
    modificador: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modificador
    ) {
        Text(
            text = "Habitat",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimario,
            letterSpacing = 2.sp
        )
        Text(
            text = "DIGITAL",
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            color = ColorSecundario,
            letterSpacing = 4.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaIniciarSesion(
    onIniciarSesionClick: () -> Unit = {},
    onCrearCuentaClick: () -> Unit = {},
    onOlvideContrasennaClick: () -> Unit = {},
    onVolverClick: () -> Unit = {},
    viewModel: InicioSesionViewModel = viewModel()
) {
    // Obtener estados del ViewModel
    val email by viewModel.email.collectAsState()
    val contrasenna by viewModel.contrasenna.collectAsState()
    val recordarme by viewModel.recordarme.collectAsState()
    val estadoInicioSesion by viewModel.estadoInicioSesion.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val formularioValido by viewModel.formularioValido.collectAsState()

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Verificar sesión existente al iniciar
    LaunchedEffect(key1 = true) {
        viewModel.verificarSesionExistente()
    }

    // Manejar navegación después del éxito
    LaunchedEffect(estadoInicioSesion) {
        if (estadoInicioSesion is EstadoInicioSesion.Exito) {
            onIniciarSesionClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
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

                // Título "Habitat Digital"
                TituloHabitatDigital(
                    modificador = Modifier.padding(bottom = 48.dp)
                )

                // Formulario de inicio de sesión
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
                            text = "Acceso al Sistema",
                            color = ColorTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            textAlign = TextAlign.Center
                        )

                        // Campo Email conectado al ViewModel
                        CampoTextoInicioSesion(
                            valor = email,
                            alCambiarValor = viewModel::actualizarEmail,
                            etiqueta = "Correo electrónico",
                            icono = Icons.Default.Email,
                            tipoTeclado = KeyboardType.Email
                        )

                        // Campo Contraseña conectado al ViewModel
                        CampoTextoInicioSesion(
                            valor = contrasenna,
                            alCambiarValor = viewModel::actualizarContrasenna,
                            etiqueta = "Contraseña",
                            icono = Icons.Default.Lock,
                            tipoTeclado = KeyboardType.Password,
                            esContrasenna = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fila con checkbox y enlace de contraseña olvidada
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CheckboxRecordarme(
                                recordarme = recordarme,
                                alCambiar = viewModel::actualizarRecordarme
                            )

                            TextButton(
                                onClick = onOlvideContrasennaClick
                            ) {
                                Text(
                                    text = "¿Olvidaste tu contraseña?",
                                    color = ColorPrimario,
                                    fontSize = 12.sp,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de iniciar sesión conectado al ViewModel
                        BotonIniciarSesion(
                            habilitado = formularioValido,
                            cargando = estadoInicioSesion is EstadoInicioSesion.Cargando,
                            onClick = {
                                scope.launch {
                                    viewModel.iniciarSesion()
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Sección de registro
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
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
                            text = "¿Aún no tienes cuenta?",
                            color = ColorSecundario,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = onCrearCuentaClick,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = ColorPrimario
                            )
                        ) {
                            Text(
                                text = "Crear nueva cuenta",
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

    // Diálogo de error
    DialogoErrorInicioSesion(
        mostrar = mensajeError != null,
        mensaje = mensajeError ?: "",
        onDismiss = viewModel::reiniciarEstado
    )
}

@Composable
fun DialogoErrorInicioSesion(
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
                    text = "Error de autenticación",
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
                        containerColor = ColorPrimario
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

@Preview(showBackground = true)
@Composable
fun PantallaIniciarSesionPreview() {
    TemaAppComunidades {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ColorFondo
        ) {
            PantallaIniciarSesionSinViewModel()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PantallaIniciarSesionSinViewModel() {
    var email by remember { mutableStateOf("") }
    var contrasenna by remember { mutableStateOf("") }
    var recordarme by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
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

                TituloHabitatDigital(
                    modificador = Modifier.padding(bottom = 48.dp)
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
                            text = "Acceso al Sistema",
                            color = ColorTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            textAlign = TextAlign.Center
                        )

                        CampoTextoInicioSesion(
                            valor = email,
                            alCambiarValor = { email = it },
                            etiqueta = "Correo electrónico",
                            icono = Icons.Default.Email,
                            tipoTeclado = KeyboardType.Email
                        )

                        CampoTextoInicioSesion(
                            valor = contrasenna,
                            alCambiarValor = { contrasenna = it },
                            etiqueta = "Contraseña",
                            icono = Icons.Default.Lock,
                            tipoTeclado = KeyboardType.Password,
                            esContrasenna = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CheckboxRecordarme(
                                recordarme = recordarme,
                                alCambiar = { recordarme = it }
                            )

                            TextButton(onClick = { }) {
                                Text(
                                    text = "¿Olvidaste tu contraseña?",
                                    color = ColorPrimario,
                                    fontSize = 12.sp,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        BotonIniciarSesion(
                            habilitado = true,
                            cargando = false,
                            onClick = { }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
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
                            text = "¿Aún no tienes cuenta?",
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
                                text = "Crear nueva cuenta",
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