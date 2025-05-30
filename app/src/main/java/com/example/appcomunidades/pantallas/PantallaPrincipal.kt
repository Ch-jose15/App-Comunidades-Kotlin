package com.example.appcomunidades.pantallas

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcomunidades.ui.theme.*

/* ENUMS Y DATA CLASSES */

enum class SeccionPrincipal(
    val titulo: String,
    val icono: ImageVector,
    val color: Color
) {
    ANUNCIOS("Anuncios", Icons.Default.Notifications, ColorPrimario),
    INCIDENCIAS("Incidencias", Icons.Default.Warning, ColorSecundario),
    USUARIOS("Usuarios", Icons.Default.AccountBox, ColorPrimarioVariante)
}

// Data classes de ejemplo para el diseño
data class AnuncioEjemplo(
    val id: String,
    val titulo: String,
    val contenido: String,
    val esUrgente: Boolean,
    val fecha: String,
    val autor: String
)

data class IncidenciaEjemplo(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val prioridad: String,
    val estado: String,
    val fecha: String
)

data class UsuarioEjemplo(
    val id: String,
    val nombre: String,
    val email: String,
    val esAdmin: Boolean
)

/* COMPONENTES ATOMIZADOS */

@Composable
fun BarraSuperior(
    onPerfilClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo lado izquierdo
        LogoHorizontal()

        // Icono de perfil lado derecho
        IconButton(
            onClick = onPerfilClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(ColorPrimario.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Perfil",
                tint = ColorPrimario,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun LogoHorizontal(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder para logo - reemplazar con tu imagen
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorPrimario),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "H",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Habitat",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTexto
            )
            Text(
                text = "DIGITAL",
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                color = ColorSecundario,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun BarraNavegacion(
    seccionActual: SeccionPrincipal,
    onSeccionSeleccionada: (SeccionPrincipal) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SeccionPrincipal.entries.forEach { seccion ->
                TabNavegacion(
                    seccion = seccion,
                    estaSeleccionado = seccionActual == seccion,
                    onClick = { onSeccionSeleccionada(seccion) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TabNavegacion(
    seccion: SeccionPrincipal,
    estaSeleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .padding(4.dp)
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionado)
                seccion.color.copy(alpha = 0.15f)
            else
                Color.Transparent
        ),
        border = if (estaSeleccionado)
            BorderStroke(2.dp, seccion.color)
        else
            null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = seccion.icono,
                contentDescription = seccion.titulo,
                tint = if (estaSeleccionado) seccion.color else ColorSecundario,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = seccion.titulo,
                fontSize = 12.sp,
                fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Normal,
                color = if (estaSeleccionado) seccion.color else ColorSecundario
            )
        }
    }
}

@Composable
fun TarjetaAnuncio(
    anuncio: AnuncioEjemplo,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = anuncio.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTexto,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = anuncio.contenido,
                        fontSize = 14.sp,
                        color = ColorSecundario,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (anuncio.esUrgente) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "URGENTE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = anuncio.autor,
                    fontSize = 12.sp,
                    color = ColorPrimarioVariante
                )
                Text(
                    text = anuncio.fecha,
                    fontSize = 12.sp,
                    color = ColorSecundario
                )
            }
        }
    }
}

@Composable
fun TarjetaIncidencia(
    incidencia: IncidenciaEjemplo,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = incidencia.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTexto,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = incidencia.descripcion,
                        fontSize = 14.sp,
                        color = ColorSecundario,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Indicador de prioridad
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (incidencia.prioridad.lowercase()) {
                            "alta" -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            "media" -> Color(0xFFFFA726).copy(alpha = 0.1f)
                            else -> Color.Green.copy(alpha = 0.1f)
                        }
                    )
                ) {
                    Text(
                        text = incidencia.prioridad.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (incidencia.prioridad.lowercase()) {
                            "alta" -> MaterialTheme.colorScheme.error
                            "media" -> Color(0xFFFF6F00)
                            else -> Color(0xFF388E3C)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Estado
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (incidencia.estado.lowercase()) {
                                    "resuelta" -> Color.Green
                                    "en proceso" -> Color(0xFFFFA726)
                                    else -> ColorSecundario
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = incidencia.estado,
                        fontSize = 12.sp,
                        color = ColorSecundario
                    )
                }

                Text(
                    text = incidencia.fecha,
                    fontSize = 12.sp,
                    color = ColorSecundario
                )
            }
        }
    }
}

@Composable
fun TarjetaUsuario(
    usuario: UsuarioEjemplo,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (usuario.esAdmin) ColorPrimario.copy(alpha = 0.2f)
                        else ColorSecundario.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.nombre.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = if (usuario.esAdmin) ColorPrimario else ColorSecundario
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = usuario.nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorTexto
                    )
                    if (usuario.esAdmin) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ColorPrimario.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = "ADMIN",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorPrimario
                            )
                        }
                    }
                }
                Text(
                    text = usuario.email,
                    fontSize = 14.sp,
                    color = ColorSecundario
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalles",
                tint = ColorSecundario
            )
        }
    }
}

@Composable
fun BotonFlotanteCrear(
    texto: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = color,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Crear",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            fontWeight = FontWeight.Medium
        )
    }
}

/* PANTALLA PRINCIPAL */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PantallaPrincipal() {
    var seccionActual by remember { mutableStateOf(SeccionPrincipal.ANUNCIOS) }

    // Datos de ejemplo
    val anunciosEjemplo = listOf(
        AnuncioEjemplo("1", "Reunión de vecinos", "Se convoca a todos los propietarios a la reunión mensual...", true, "Hace 2 horas", "Admin"),
        AnuncioEjemplo("2", "Mantenimiento ascensor", "El próximo lunes se realizará el mantenimiento...", false, "Ayer", "Admin"),
        AnuncioEjemplo("3", "Horario de piscina", "Recordamos que el horario de verano de la piscina...", false, "Hace 3 días", "Admin")
    )

    val incidenciasEjemplo = listOf(
        IncidenciaEjemplo("1", "Fuga de agua en garaje", "Se ha detectado una fuga en el nivel -1", "alta", "En proceso", "Hoy"),
        IncidenciaEjemplo("2", "Luz fundida en portal", "La luz del portal principal no funciona", "media", "Pendiente", "Ayer"),
        IncidenciaEjemplo("3", "Ruidos molestos 3ºB", "Ruidos excesivos en horario nocturno", "baja", "Resuelta", "Hace 5 días")
    )

    val usuariosEjemplo = listOf(
        UsuarioEjemplo("1", "María García", "maria@example.com", true),
        UsuarioEjemplo("2", "Juan Pérez", "juan@example.com", false),
        UsuarioEjemplo("3", "Ana López", "ana@example.com", false)
    )

    Scaffold(
        containerColor = ColorFondo,
        floatingActionButton = {
            AnimatedVisibility(
                visible = seccionActual != SeccionPrincipal.USUARIOS,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                BotonFlotanteCrear(
                    texto = when (seccionActual) {
                        SeccionPrincipal.ANUNCIOS -> "Nuevo Anuncio"
                        SeccionPrincipal.INCIDENCIAS -> "Nueva Incidencia"
                        else -> ""
                    },
                    onClick = { /* Acción de crear */ },
                    color = seccionActual.color
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ColorFondo, ColorFondoSecundario.copy(alpha = 0.3f)),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        ) {
            // Barra superior con logo y perfil
            BarraSuperior(
                onPerfilClick = { /* Navegar a perfil */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de navegación
            BarraNavegacion(
                seccionActual = seccionActual,
                onSeccionSeleccionada = { seccionActual = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido según la sección
            AnimatedContent(
                targetState = seccionActual,
                transitionSpec = {
                    fadeIn() with fadeOut()
                }
            ) { seccion ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (seccion) {
                        SeccionPrincipal.ANUNCIOS -> {
                            items(anunciosEjemplo) { anuncio ->
                                TarjetaAnuncio(
                                    anuncio = anuncio,
                                    onClick = { /* Ver detalle */ }
                                )
                            }
                        }
                        SeccionPrincipal.INCIDENCIAS -> {
                            items(incidenciasEjemplo) { incidencia ->
                                TarjetaIncidencia(
                                    incidencia = incidencia,
                                    onClick = { /* Ver detalle */ }
                                )
                            }
                        }
                        SeccionPrincipal.USUARIOS -> {
                            items(usuariosEjemplo) { usuario ->
                                TarjetaUsuario(
                                    usuario = usuario,
                                    onClick = { /* Ver detalle */ }
                                )
                            }
                        }
                    }

                    // Espacio extra al final para el FAB
                    if (seccion != SeccionPrincipal.USUARIOS) {
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaPrincipalPreview() {
    TemaAppComunidades {
        PantallaPrincipal()
    }
}