package com.example.appcomunidades.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcomunidades.ui.theme.*

/* PANTALLA DE INICIO MODERNIZADA */

@Composable
fun TituloHabitatDigitalInicio(
    modificador: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modificador
    ) {
        Text(
            text = "Habitat",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimario,
            letterSpacing = 3.sp
        )
        Text(
            text = "DIGITAL",
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            color = ColorSecundario,
            letterSpacing = 6.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Gestión inteligente de comunidades",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = ColorPrimarioVariante,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun BotonPrimarioInicio(
    texto: String,
    onClick: () -> Unit,
    modificador: Modifier = Modifier,
    colorFondo: Color = ColorPrimario
) {
    Button(
        onClick = onClick,
        modifier = modificador
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = texto,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BotonSecundarioInicio(
    texto: String,
    onClick: () -> Unit,
    modificador: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modificador
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ColorPrimario,
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = ColorPrimario
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = texto,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CopyrightTexto(
    modificador: Modifier = Modifier
) {
    Text(
        text = "© 2025 Jose Manuel Jara",
        fontSize = 10.sp,
        color = ColorSecundario.copy(alpha = 0.7f),
        modifier = modificador
    )
}

@Composable
fun PantallaInicio(
    onIniciarSesionClick: () -> Unit = {},
    onRegistrarseClick: () -> Unit = {},
    onCrearComunidadClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Título principal
            TituloHabitatDigitalInicio(
                modificador = Modifier.padding(bottom = 64.dp)
            )

            // Card con los botones
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Acceso al Sistema",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTexto,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Botón de inicio de sesión
                    BotonPrimarioInicio(
                        texto = "Iniciar Sesión",
                        onClick = onIniciarSesionClick
                    )

                    // Botón de registro
                    BotonSecundarioInicio(
                        texto = "Registrarse",
                        onClick = onRegistrarseClick
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Divider
                    HorizontalDivider(
                        color = ColorPrimarioVariante.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Administración",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorSecundario,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Botón de crear comunidad
                    BotonPrimarioInicio(
                        texto = "Crear Nueva Comunidad",
                        onClick = onCrearComunidadClick,
                        colorFondo = ColorSecundario
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // Copyright en la esquina inferior derecha
        CopyrightTexto(
            modificador = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaInicioPreview() {
    TemaAppComunidades {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ColorFondo
        ) {
            PantallaInicio()
        }
    }
}