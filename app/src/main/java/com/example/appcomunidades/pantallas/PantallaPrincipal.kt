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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcomunidades.ui.theme.*
import com.example.appcomunidades.componentes.*
import com.example.appcomunidades.viewmodels.EstadoPerfil

/* ENUMS Y DATA CLASSES DE LA PANTALLA PRINCIPAL */

enum class SeccionPrincipal(
    val titulo: String,
    val icono: ImageVector,
    val color: Color
) {
    ANUNCIOS("Anuncios", Icons.Default.Notifications, Color(0xFF06141B)),
    INCIDENCIAS("Incidencias", Icons.Default.Warning, Color(0xFF253745)),
    USUARIOS("Usuarios", Icons.Default.AccountBox, Color(0xFF4A5C6A))
}

// Data classes de ejemplo para el diseño
data class AnuncioEjemplo(
    val id: String,
    val titulo: String,
    val contenido: String,
    val esUrgente: Boolean,
    val fecha: String,
    val autor: String,
    val categoria: String = "",
    val colorCategoria: Long = 0xFF616161,
    val comunidadId: String = ""
)

data class UsuarioEjemplo(
    val id: String,
    val nombre: String,
    val email: String,
    val esAdmin: Boolean
)

/* COMPONENTES ESPECÍFICOS DE LA PANTALLA PRINCIPAL */

@Composable
fun BarraSuperior(
    onPerfilClick: () -> Unit = {},
    datosUsuario: DatosPerfilUsuario? = null,
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

        // Avatar del usuario lado derecho
        if (datosUsuario != null) {
            IconButton(
                onClick = onPerfilClick,
                modifier = Modifier.size(48.dp)
            ) {
                AvatarUsuario(
                    datos = datosUsuario,
                    tamano = 44
                )
            }
        } else {
            // Fallback al icono por defecto
            IconButton(
                onClick = onPerfilClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF06141B).copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    tint = Color(0xFF06141B),
                    modifier = Modifier.size(32.dp)
                )
            }
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
                .background(Color(0xFF06141B)),
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
                color = Color(0xFF253745)
            )
            Text(
                text = "DIGITAL",
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF4A5C6A),
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
                tint = if (estaSeleccionado) seccion.color else Color(0xFF9BA8AB),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = seccion.titulo,
                fontSize = 12.sp,
                fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Normal,
                color = if (estaSeleccionado) seccion.color else Color(0xFF9BA8AB)
            )
        }
    }
}

/* COMPONENTES DE TARJETAS */

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
            // Fila superior: Título y etiqueta de urgente
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
                        color = Color(0xFF253745),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = anuncio.contenido,
                        fontSize = 14.sp,
                        color = Color(0xFF4A5C6A),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Etiqueta de urgente
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

            // Fila inferior: Autor, fecha, categoría y comunidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna izquierda: Autor y comunidad
                Column {
                    Text(
                        text = "Por: ${anuncio.autor}",
                        fontSize = 12.sp,
                        color = Color(0xFF06141B),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = anuncio.fecha,
                        fontSize = 11.sp,
                        color = Color(0xFF9BA8AB)
                    )
                    // Mostrar comunidad si no está vacía
                    if (anuncio.comunidadId.isNotEmpty()) {
                        Text(
                            text = "📍 ${anuncio.comunidadId}",
                            fontSize = 10.sp,
                            color = Color(0xFF9BA8AB).copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Columna derecha: Categoría (si está disponible)
                if (anuncio.categoria.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(anuncio.colorCategoria).copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = anuncio.categoria,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(anuncio.colorCategoria)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaIncidenciaSimple(
    incidencia: com.example.appcomunidades.modelos.Incidencia,
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
            // Fila superior: Título y prioridad
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
                        color = Color(0xFF253745),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = incidencia.descripcion,
                        fontSize = 14.sp,
                        color = Color(0xFF4A5C6A),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Etiqueta de prioridad
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (incidencia.prioridad) {
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
                        color = when (incidencia.prioridad) {
                            "alta" -> MaterialTheme.colorScheme.error
                            "media" -> Color(0xFFFF6F00)
                            else -> Color(0xFF388E3C)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fila inferior: Autor, fecha, categoría y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna izquierda: Autor y comunidad
                Column {
                    Text(
                        text = "Por: ${incidencia.nombre_autor}",
                        fontSize = 12.sp,
                        color = Color(0xFF06141B),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = incidencia.fecha_formateada,
                        fontSize = 11.sp,
                        color = Color(0xFF9BA8AB)
                    )
                    if (incidencia.comunidad_id.isNotEmpty()) {
                        Text(
                            text = "📍 ${incidencia.comunidad_id}",
                            fontSize = 10.sp,
                            color = Color(0xFF9BA8AB).copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Columna derecha: Estado
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (incidencia.estado) {
                                    "resuelta" -> Color.Green
                                    "en proceso" -> Color(0xFFFFA726)
                                    else -> Color(0xFF9BA8AB)
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = incidencia.estado,
                        fontSize = 12.sp,
                        color = Color(0xFF4A5C6A)
                    )
                }
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
                        if (usuario.esAdmin) Color(0xFF06141B).copy(alpha = 0.2f)
                        else Color(0xFF4A5C6A).copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.nombre.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = if (usuario.esAdmin) Color(0xFF06141B) else Color(0xFF4A5C6A)
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
                        color = Color(0xFF253745)
                    )
                    if (usuario.esAdmin) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF06141B).copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = "ADMIN",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF06141B)
                            )
                        }
                    }
                }
                Text(
                    text = usuario.email,
                    fontSize = 14.sp,
                    color = Color(0xFF4A5C6A)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalles",
                tint = Color(0xFF9BA8AB)
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
fun PantallaPrincipal(
    onCrearAnuncioClick: () -> Unit = {},
    onCrearIncidenciaClick: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    viewModel: com.example.appcomunidades.viewmodels.PantallaPrincipalViewModel = viewModel(),
    perfilViewModel: com.example.appcomunidades.viewmodels.PerfilViewModel = viewModel()
) {
    var seccionActual by remember { mutableStateOf(SeccionPrincipal.ANUNCIOS) }

    // Estados para el perfil
    var mostrarBottomSheetPerfil by remember { mutableStateOf(false) }
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    // Estados del ViewModel principal para ANUNCIOS
    val anunciosReales by viewModel.anunciosParaUI.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    // Estados del ViewModel principal para INCIDENCIAS
    val incidencias by viewModel.incidencias.collectAsState()
    val estaCargandoIncidencias by viewModel.estaCargandoIncidencias.collectAsState()
    val mensajeErrorIncidencias by viewModel.mensajeErrorIncidencias.collectAsState()

    // Estados del ViewModel de perfil
    val estadoPerfil by perfilViewModel.estadoPerfil.collectAsState()
    val datosPerfil by perfilViewModel.datosPerfil.collectAsState()
    val estadoCerrarSesion by perfilViewModel.estadoCerrarSesion.collectAsState()

    // Manejar el resultado de cerrar sesión
    LaunchedEffect(estadoCerrarSesion) {
        when (estadoCerrarSesion) {
            is com.example.appcomunidades.viewmodels.EstadoCerrarSesion.Completado -> {
                mostrarDialogoCerrarSesion = false
                mostrarBottomSheetPerfil = false
                onCerrarSesion()
                perfilViewModel.reiniciarEstadoCerrarSesion()
            }
            else -> { /* No hacer nada */ }
        }
    }

    // Función para manejar cerrar sesión
    val manejarCerrarSesion = {
        perfilViewModel.cerrarSesion()
    }

    // Refrescar datos cuando se regrese a la pantalla
    LaunchedEffect(key1 = true) {
        viewModel.refrescarAnuncios()
        viewModel.refrescarIncidencias()
        perfilViewModel.refrescarPerfil()
    }

    // Datos de ejemplo para usuarios (mantener hasta implementar)
    val usuariosEjemplo = listOf(
        UsuarioEjemplo("1", "María García", "maria@example.com", true),
        UsuarioEjemplo("2", "Juan Pérez", "juan@example.com", false),
        UsuarioEjemplo("3", "Ana López", "ana@example.com", false)
    )

    Scaffold(
        containerColor = Color(0xFFCCD0CF),
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
                    onClick = {
                        when (seccionActual) {
                            SeccionPrincipal.ANUNCIOS -> onCrearAnuncioClick()
                            SeccionPrincipal.INCIDENCIAS -> onCrearIncidenciaClick()
                            else -> { /* No hacer nada */ }
                        }
                    },
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
                        colors = listOf(Color(0xFFCCD0CF), Color(0xFF9BA8AB).copy(alpha = 0.3f)),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        ) {
            // Barra superior con logo y perfil
            BarraSuperior(
                onPerfilClick = { mostrarBottomSheetPerfil = true },
                datosUsuario = datosPerfil ?: perfilViewModel.obtenerDatosBasicos()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de navegación
            BarraNavegacion(
                seccionActual = seccionActual,
                onSeccionSeleccionada = { seccionActual = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar mensaje de error si hay (ANUNCIOS)
            mensajeError?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = {
                                viewModel.limpiarError()
                                viewModel.refrescarAnuncios()
                            }
                        ) {
                            Text("Reintentar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

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
                            // Mostrar indicador de carga si está cargando
                            if (estaCargando) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color(0xFF06141B),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Cargando anuncios...",
                                                color = Color(0xFF4A5C6A)
                                            )
                                        }
                                    }
                                }
                            }

                            // Mostrar anuncios reales
                            if (anunciosReales.isNotEmpty()) {
                                items(anunciosReales) { anuncio ->
                                    TarjetaAnuncio(
                                        anuncio = anuncio,
                                        onClick = { /* Ver detalle */ }
                                    )
                                }
                            } else if (!estaCargando) {
                                // Mostrar mensaje si no hay anuncios
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF06141B).copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = "Sin anuncios",
                                                tint = Color(0xFF06141B),
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "No hay anuncios aún",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF253745)
                                            )
                                            Text(
                                                text = "¡Sé el primero en crear un anuncio!",
                                                fontSize = 14.sp,
                                                color = Color(0xFF4A5C6A),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        SeccionPrincipal.INCIDENCIAS -> {
                            // Mostrar mensaje de error si hay (INCIDENCIAS)
                            mensajeErrorIncidencias?.let { error ->
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "Error",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = error,
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 12.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            TextButton(
                                                onClick = {
                                                    viewModel.limpiarErrorIncidencias()
                                                    viewModel.refrescarIncidencias()
                                                }
                                            ) {
                                                Text("Reintentar", color = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }

                            // Mostrar indicador de carga
                            if (estaCargandoIncidencias) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color(0xFF06141B),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Cargando incidencias...",
                                                color = Color(0xFF4A5C6A)
                                            )
                                        }
                                    }
                                }
                            }

                            // Mostrar incidencias
                            if (incidencias.isNotEmpty()) {
                                items(incidencias) { incidencia ->
                                    TarjetaIncidenciaSimple(
                                        incidencia = incidencia,
                                        onClick = { /* Ver detalle */ }
                                    )
                                }
                            } else if (!estaCargandoIncidencias && mensajeErrorIncidencias == null) {
                                // Mostrar mensaje si no hay incidencias
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF253745).copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "Sin incidencias",
                                                tint = Color(0xFF253745),
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "No hay incidencias aún",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF253745)
                                            )
                                            Text(
                                                text = "¡Sé el primero en reportar una incidencia!",
                                                fontSize = 14.sp,
                                                color = Color(0xFF4A5C6A),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
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

    // BottomSheet del perfil
    if (datosPerfil != null) {
        BottomSheetPerfil(
            mostrar = mostrarBottomSheetPerfil,
            onDismiss = { mostrarBottomSheetPerfil = false },
            datos = datosPerfil!!,
            onCerrarSesion = { mostrarDialogoCerrarSesion = true },
            estaCargandoCerrarSesion = estadoCerrarSesion is com.example.appcomunidades.viewmodels.EstadoCerrarSesion.Cargando
        )
    }

    // Diálogo de confirmación para cerrar sesión
    DialogoConfirmarCerrarSesion(
        mostrar = mostrarDialogoCerrarSesion,
        onConfirmar = manejarCerrarSesion,
        onCancelar = { mostrarDialogoCerrarSesion = false }
    )

    // Mostrar error de perfil si hay alguno
    LaunchedEffect(estadoPerfil) {
        if (estadoPerfil is com.example.appcomunidades.viewmodels.EstadoPerfil.Error) {
            println("DEBUG: Error en perfil: ${(estadoPerfil as EstadoPerfil.Error).mensaje}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaPrincipalPreview() {
    TemaAppComunidades {
        PantallaPrincipal(
            onCerrarSesion = { /* Simular cerrar sesión */ }
        )
    }
}