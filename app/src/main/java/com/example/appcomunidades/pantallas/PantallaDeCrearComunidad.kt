package com.example.appcomunidades.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcomunidades.ui.theme.*
import kotlinx.coroutines.launch

/* COMPONENTES MODERNIZADOS PARA CREAR COMUNIDAD */

@Composable
fun SelectorImagenComunidad(
    onClick: () -> Unit,
    modificador: Modifier = Modifier
) {
    Box(
        modifier = modificador
            .size(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(ColorPrimario.copy(alpha = 0.1f), ColorPrimarioVariante.copy(alpha = 0.2f)),
                    radius = 300f
                )
            )
            .border(
                width = 2.dp,
                color = ColorPrimario.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Seleccionar imagen",
                modifier = Modifier.size(40.dp),
                tint = ColorPrimario
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Imagen de\nla comunidad",
                color = ColorSecundario,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoComunidad(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    esMultilinea: Boolean = false,
    modificador: Modifier = Modifier
) {
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
        singleLine = !esMultilinea,
        minLines = if (esMultilinea) 3 else 1,
        maxLines = if (esMultilinea) 5 else 1,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = ColorPrimario,
            focusedBorderColor = ColorPrimario,
            unfocusedBorderColor = ColorPrimarioVariante,
            focusedLabelColor = ColorPrimario,
            unfocusedLabelColor = ColorPrimarioVariante,
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = if (esMultilinea)
            KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        else
            KeyboardOptions.Default,
        modifier = modificador
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun BotonCrearComunidad(
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
            containerColor = ColorSecundario,
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
            Text(
                text = "CREAR COMUNIDAD",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun TarjetaInformacion(
    modificador: Modifier = Modifier
) {
    Card(
        modifier = modificador.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ColorPrimario.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = ColorPrimario.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Información",
                tint = ColorPrimario,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Generación automática",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTexto
                )
                Text(
                    text = "El ID único y fecha de creación se asignarán automáticamente",
                    fontSize = 12.sp,
                    color = ColorSecundario,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/* PANTALLA PARA CREAR COMUNIDADES MODERNIZADA */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearComunidad(
    onVolverClick: () -> Unit = {},
    onCrearComunidadClick: () -> Unit = {}
) {
    var nombreComunidad by remember { mutableStateOf("") }
    var direccionComunidad by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nueva Comunidad") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorSecundario,
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

                // Selector de imagen
                SelectorImagenComunidad(
                    onClick = {
                        // Lógica para seleccionar imagen (implementar más tarde)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                            text = "Información de la Comunidad",
                            color = ColorTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            textAlign = TextAlign.Center
                        )

                        // Campo nombre
                        CampoTextoComunidad(
                            valor = nombreComunidad,
                            alCambiarValor = { nombreComunidad = it },
                            etiqueta = "Nombre de la Comunidad",
                            icono = Icons.Default.Home
                        )

                        // Campo dirección
                        CampoTextoComunidad(
                            valor = direccionComunidad,
                            alCambiarValor = { direccionComunidad = it },
                            etiqueta = "Dirección Completa",
                            icono = Icons.Default.LocationOn,
                            esMultilinea = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tarjeta informativa
                        TarjetaInformacion()
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón crear
                BotonCrearComunidad(
                    habilitado = nombreComunidad.isNotBlank() && direccionComunidad.isNotBlank(),
                    cargando = cargando,
                    onClick = {
                        scope.launch {
                            cargando = true
                            // Simular proceso de creación
                            kotlinx.coroutines.delay(1500)
                            cargando = false
                            onCrearComunidadClick()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaCrearComunidadPreview() {
    TemaAppComunidades {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ColorFondo
        ) {
            PantallaCrearComunidad()
        }
    }
}