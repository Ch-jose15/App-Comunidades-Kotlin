package com.example.appcomunidades.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcomunidades.R
import com.example.appcomunidades.ui.theme.*

/* PANTALLA DE INICIO - LOGIN */

@Composable
fun LogoApp(
    modificador: Modifier = Modifier
) {
    Image(
         painter = painterResource(id = R.drawable.logo_app),
         contentDescription = "Logo Habitat Digital",
         modifier = modificador.size(400.dp),
         contentScale = ContentScale.Fit
    )
}

@Composable
fun BotonPrimarioInicio(
    texto: String,
    onClick: () -> Unit,
    modificador: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modificador
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPrimario,
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
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
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
            containerColor = Color.Transparent
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
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
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
fun ContenedorBotones(
    onIniciarSesionClick: () -> Unit,
    onRegistrarseClick: () -> Unit,
    modificador: Modifier = Modifier
) {
    Card(
        modifier = modificador.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón de inicio de sesión
            BotonPrimarioInicio(
                texto = "Iniciar Sesión",
                onClick = onIniciarSesionClick
            )

            // Botón de registro
            BotonSecundarioInicio(
                texto = "Crear Cuenta",
                onClick = onRegistrarseClick
            )
        }
    }
}

@Composable
fun PantallaInicio(
    onIniciarSesionClick: () -> Unit = {},
    onRegistrarseClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ColorFondo,
                        ColorFondoSecundario.copy(alpha = 0.3f)
                    ),
                    startY = 0f,
                    endY = 1000f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            LogoApp()

            Spacer(modifier = Modifier.height(24.dp))

            // Contenedor con botones
            ContenedorBotones(
                onIniciarSesionClick = onIniciarSesionClick,
                onRegistrarseClick = onRegistrarseClick
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        CopyrightTexto(
            modificador = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
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