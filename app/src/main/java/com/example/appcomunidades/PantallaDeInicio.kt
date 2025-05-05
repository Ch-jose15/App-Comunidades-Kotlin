package com.example.appcomunidades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcomunidades.ui.theme.*

@Composable
fun PantallaInicio(
    onIniciarSesionClick: () -> Unit = {},
    onRegistrarseClick: () -> Unit = {},
    onCrearComunidadClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            /* Espacio para el logo */
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(60.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOGO",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            /* Nombre de la App */
            Text(
                text = "Habitat Digital",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(40.dp))

            /* Botón de inicio de sesion */
            Button(
                onClick = onIniciarSesionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            /* Botón de registro */
            OutlinedButton(
                onClick = onRegistrarseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Registrarse",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            /* Botón de crear comunidad */
            Button(
                onClick = onCrearComunidadClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Crear Comunidad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaInicioPreview() {
    TemaAppComunidades {
        PantallaInicio()
    }
}