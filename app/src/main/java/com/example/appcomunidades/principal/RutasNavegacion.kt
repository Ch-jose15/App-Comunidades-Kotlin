package com.example.appcomunidades.principal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appcomunidades.pantallas.PantallaIniciarSesion
import com.example.appcomunidades.pantallas.PantallaInicio
import com.example.appcomunidades.pantallas.PantallaRegistro
import com.example.appcomunidades.pantallas.PantallaPrincipal

/* RUTAS DE NAVEGACIÓN */

/*------------------------------------------------------------------------------------------------*/

/* Definir todas las rutas de navegación disponibles */

enum class RutasNavegacion {
    Inicio,
    IniciarSesion,
    Registro,
    RecuperarContrasenna,
    Principal,
    PantallaPrincipal;

    companion object {
        fun fromRoute(ruta: String?): RutasNavegacion =
            when (ruta?.substringBefore("/")){
                Inicio.name -> Inicio
                IniciarSesion.name -> IniciarSesion
                Registro.name -> Registro
                RecuperarContrasenna.name -> RecuperarContrasenna
                Principal.name -> Principal
                PantallaPrincipal.name -> PantallaPrincipal
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
                onRegistrarseClick = accionesNavegacion.navegarARegistro
            )
        }

        /* Definición de IniciarSesion */
        composable(RutasNavegacion.IniciarSesion.name) {
            PantallaIniciarSesion(
                onIniciarSesionClick = accionesNavegacion.navegarAPrincipal,
                onCrearCuentaClick = accionesNavegacion.navegarARegistro,
                onOlvideContrasennaClick = accionesNavegacion.navegarARecuperarContrasenna,
                onVolverClick = accionesNavegacion.volver
            )
        }

        /* Definición de Registro */
        composable(RutasNavegacion.Registro.name) {
            PantallaRegistro(
                onVolverClick = accionesNavegacion.volver,
                onRegistrarseClick = {
                    // Implementar la lógica para guardar el usuario en Firebase
                    accionesNavegacion.navegarAPrincipal()
                }
            )
        }

        /* Definición de Principal */
        composable(RutasNavegacion.Principal.name) {
            PantallaPrincipal()
        }
    }
}