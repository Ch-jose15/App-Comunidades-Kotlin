package com.example.appcomunidades

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/*------------------------------------------------------------------------------------------------*/

/* Definir todas las rutas de navegación disponibles */

enum class RutasNavegacion {
    Inicio,
    IniciarSesion,
    Registro,
    RecuperarContrasenna,
    Principal,
    CrearComunidad;

    companion object {
        fun fromRoute(ruta: String?): RutasNavegacion =
            when (ruta?.substringBefore("/")){
                Inicio.name -> Inicio
                IniciarSesion.name -> IniciarSesion
                Registro.name -> Registro
                RecuperarContrasenna.name -> RecuperarContrasenna
                Principal.name -> Principal
                CrearComunidad.name -> CrearComunidad
                null -> Inicio
                else -> throw IllegalArgumentException("Ruta $ruta no reconocida" )
            }
    }
}

/*------------------------------------------------------------------------------------------------*/

/* Manejar las acciones de navegación entre pantallas */

class AccionesDeNavegacion(private val navController : NavHostController) {

    /* Navegación hacia atrás */
    val volver: () -> Unit = {
        navController.popBackStack()
    }

    /* Navegación a la pantalla de inicio */
    val navegarAInicio: () -> Unit = {
        navController.navigate(RutasNavegacion.Inicio.name){
            popUpTo(RutasNavegacion.Inicio.name) { inclusive = true }
        }
    }

    /* Navegación a la pantalla de inicio de sesion */
    val navegarAIniciarSesion: () -> Unit = {
        navController.navigate(RutasNavegacion.IniciarSesion.name)
    }

    /* Navegación a la pantalla de registro */
    val navegarARegistro: () -> Unit = {
        navController.navigate(RutasNavegacion.Registro.name)
    }

    /* Navegación a la pantalla de recuperación de contraseña */
    val navegarARecuperarContrasenna: () -> Unit = {
        navController.navigate(RutasNavegacion.RecuperarContrasenna.name)
    }

    /* Navegación a la pantalla principal (después de iniciar sesión) */
    val navegarAPrincipal: () -> Unit = {
        navController.navigate(RutasNavegacion.Principal.name) {
            // Limpiar para que el usuario no pueda volver a las pantallas de autenticación
            popUpTo(RutasNavegacion.Inicio.name) { inclusive = true }
        }
    }

    /* Navegación a la pantalla de crear comunidad */
    val navegarACrearComunidad: () -> Unit = {
        navController.navigate(RutasNavegacion.CrearComunidad.name)
    }
}

/*------------------------------------------------------------------------------------------------*/

/* Configurar el grafo de navegación */

@Composable
fun NavegacionAppComunidaeds(
    iniciarEnPantalla : RutasNavegacion = RutasNavegacion.Inicio,
    navController : NavHostController = rememberNavController()
){

    /* Evita recrear las acciones de navegación en cada recomposición */
    val accionesNavegacion = remember(navController) { AccionesDeNavegacion(navController) }

    NavHost(
        navController = navController,
        startDestination = iniciarEnPantalla.name
    ) {

        /* Definición de Inicio */
        composable(RutasNavegacion.Inicio.name) {
            PantallaInicio(
                onIniciarSesionClick = accionesNavegacion.navegarAIniciarSesion,
                onRegistrarseClick = accionesNavegacion.navegarARegistro,
                onCrearComunidadClick = accionesNavegacion.navegarACrearComunidad
            )
        }

        /* Definición de IniciarSesion */
        composable(RutasNavegacion.IniciarSesion.name) {
            PantallaIniciarSesion(
                onIniciarSesionClick = accionesNavegacion.navegarAPrincipal,
                onCrearCuentaClick = accionesNavegacion.navegarARegistro,
                onOlvideContrasennaClick = accionesNavegacion.navegarARecuperarContrasenna
            )
        }

        /* Definición de CrearComunidad */
        composable(RutasNavegacion.CrearComunidad.name) {
            PantallaCrearComunidad(
                onVolverClick = accionesNavegacion.volver,
                onCrearComunidadClick = {
                    // Implementar la lógica para guardar la comunidad en Firebase
                    accionesNavegacion.navegarAPrincipal()
                }
            )
        }
    }
}