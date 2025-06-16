package com.example.appcomunidades.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcomunidades.ui.theme.*

/**
 * Data class para representar los datos del perfil del usuario
 */
data class DatosPerfilUsuario(
    val nombre: String,
    val email: String,
    val comunidadId: String,
    val esAdmin: Boolean = false,
    val iniciales: String = ""
) {
    fun obtenerIniciales(): String {
        return if (iniciales.isNotEmpty()) {
            iniciales
        } else {
            nombre.split(" ")
                .take(2)
                .map { it.firstOrNull()?.uppercaseChar() ?: "" }
                .joinToString("")
                .take(2)
        }
    }
}

/**
 * Componente del avatar del usuario
 */
@Composable
fun AvatarUsuario(
    datos: DatosPerfilUsuario,
    tamano: Int = 64,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(tamano.dp)
            .clip(CircleShape)
            .background(
                if (datos.esAdmin)
                    Color(0xFF06141B)
                else
                    Color(0xFF4A5C6A)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = datos.obtenerIniciales(),
            color = Color.White,
            fontSize = (tamano * 0.3).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Fila de información del perfil
 */
@Composable
fun FilaInformacionPerfil(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = etiqueta,
            tint = Color(0xFF4A5C6A),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = etiqueta,
                fontSize = 12.sp,
                color = Color(0xFF9BA8AB),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = valor,
                fontSize = 14.sp,
                color = Color(0xFF253745),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

/**
 * Etiqueta de rol (Admin/Usuario)
 */
@Composable
fun EtiquetaRol(
    esAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esAdmin)
                Color(0xFF06141B).copy(alpha = 0.1f)
            else
                Color(0xFF4A5C6A).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (esAdmin) Icons.Default.Settings else Icons.Default.Person,
                contentDescription = "Rol",
                tint = if (esAdmin) Color(0xFF06141B) else Color(0xFF4A5C6A),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (esAdmin) "ADMINISTRADOR" else "VECINO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (esAdmin) Color(0xFF06141B) else Color(0xFF4A5C6A)
            )
        }
    }
}

/**
 * Botón de cerrar sesión
 */
@Composable
fun BotonCerrarSesion(
    onClick: () -> Unit,
    estaCargando: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = !estaCargando,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF06141B)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = Color(0xFF06141B)
        )
    ) {
        if (estaCargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFF06141B),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrando sesión...",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        } else {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrar Sesión",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * BottomSheet del perfil de usuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPerfil(
    mostrar: Boolean,
    onDismiss: () -> Unit,
    datos: DatosPerfilUsuario,
    onCerrarSesion: () -> Unit,
    estaCargandoCerrarSesion: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (mostrar) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = modifier,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            containerColor = Color.White,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF9BA8AB))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Cabecera con avatar y nombre
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarUsuario(
                        datos = datos,
                        tamano = 64
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = datos.nombre,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF06141B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        EtiquetaRol(esAdmin = datos.esAdmin)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divisor
                HorizontalDivider(
                    color = Color(0xFFCCD0CF),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Información del usuario
                Text(
                    text = "Información Personal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF253745),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                FilaInformacionPerfil(
                    icono = Icons.Default.Email,
                    etiqueta = "Correo Electrónico",
                    valor = datos.email
                )

                FilaInformacionPerfil(
                    icono = Icons.Default.Home,
                    etiqueta = "Comunidad",
                    valor = if (datos.comunidadId.isNotEmpty()) datos.comunidadId else "Sin asignar"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de cerrar sesión
                BotonCerrarSesion(
                    onClick = onCerrarSesion,
                    estaCargando = estaCargandoCerrarSesion
                )

                // Espacio adicional para el área de gestos
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Diálogo de confirmación para cerrar sesión
 */
@Composable
fun DialogoConfirmarCerrarSesion(
    mostrar: Boolean,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    if (mostrar) {
        AlertDialog(
            onDismissRequest = onCancelar,
            icon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint = Color(0xFF06141B),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    color = Color(0xFF06141B),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres cerrar sesión? Tendrás que volver a iniciar sesión para acceder a la aplicación.",
                    color = Color(0xFF4A5C6A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF06141B)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cerrar Sesión", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onCancelar,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF4A5C6A)
                    )
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/* PREVIEWS */

@Preview(showBackground = true)
@Composable
fun AvatarUsuarioPreview() {
    TemaAppComunidades {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AvatarUsuario(
                datos = DatosPerfilUsuario(
                    nombre = "María García",
                    email = "maria@example.com",
                    comunidadId = "Cs0000001a",
                    esAdmin = true
                )
            )
            AvatarUsuario(
                datos = DatosPerfilUsuario(
                    nombre = "Juan Pérez",
                    email = "juan@example.com",
                    comunidadId = "Cs0000001a",
                    esAdmin = false
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilaInformacionPerfilPreview() {
    TemaAppComunidades {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            FilaInformacionPerfil(
                icono = Icons.Default.Email,
                etiqueta = "Correo Electrónico",
                valor = "maria.garcia@example.com"
            )
            FilaInformacionPerfil(
                icono = Icons.Default.Home,
                etiqueta = "Comunidad",
                valor = "Cs0000001a"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetPerfilPreview() {
    TemaAppComunidades {
        BottomSheetPerfil(
            mostrar = true,
            onDismiss = { },
            datos = DatosPerfilUsuario(
                nombre = "María García Rodríguez",
                email = "maria.garcia@example.com",
                comunidadId = "Cs0000001a",
                esAdmin = true
            ),
            onCerrarSesion = { }
        )
    }
}