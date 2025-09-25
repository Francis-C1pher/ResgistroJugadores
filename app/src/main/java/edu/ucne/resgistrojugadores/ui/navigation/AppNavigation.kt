package edu.ucne.RegistroJugadores.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.ucne.RegistroJugadores.ui.screens.MenuPrincipalScreen
import edu.ucne.RegistroJugadores.ui.screens.RegistroJugadorScreen
import edu.ucne.RegistroJugadores.ui.screens.RegistroLogroScreen
import edu.ucne.RegistroJugadores.ui.screens.RegistroPartidaScreen

// Rutas de navegación
object AppRoutes {
    const val MENU_PRINCIPAL = "menu_principal"
    const val REGISTRO_JUGADORES = "registro_jugadores"
    const val REGISTRO_PARTIDAS = "registro_partidas"
    const val REGISTRO_LOGROS = "registro_logros"  // ✅ NUEVA RUTA
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.MENU_PRINCIPAL
    ) {
        // Pantalla del menú principal
        composable(AppRoutes.MENU_PRINCIPAL) {
            MenuPrincipalScreen(navController = navController)
        }

        // Pantalla de registro de jugadores
        composable(AppRoutes.REGISTRO_JUGADORES) {
            RegistroJugadorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de registro de partidas
        composable(AppRoutes.REGISTRO_PARTIDAS) {
            RegistroPartidaScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ✅ NUEVA PANTALLA: Registro de logros
        composable(AppRoutes.REGISTRO_LOGROS) {
            RegistroLogroScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}