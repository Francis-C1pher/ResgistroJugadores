package edu.ucne.RegistroJugadores.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.ucne.RegistroJugadores.JugadorApplication
import edu.ucne.RegistroJugadores.ui.navigation.AppRoutes
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroJugadorViewModel
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroJugadorViewModelFactory
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroPartidaViewModel
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroPartidaViewModelFactory

@Composable
fun MenuPrincipalScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val application = context.applicationContext as JugadorApplication

    // ViewModel para obtener estadísticas de jugadores
    val jugadorViewModel: RegistroJugadorViewModel = viewModel(
        factory = RegistroJugadorViewModelFactory(
            application.getJugadoresUseCase,
            application.insertJugadorUseCase,
            application.deleteJugadorUseCase,
            application.validateJugadorUseCase
        )
    )

    // ✅ AGREGAR: ViewModel para obtener estadísticas de partidas
    val partidaViewModel: RegistroPartidaViewModel = viewModel(
        factory = RegistroPartidaViewModelFactory(
            application.getPartidasUseCase,
            application.insertPartidaUseCase,
            application.deletePartidaUseCase,
            application.validatePartidaUseCase,
            application.getJugadoresUseCase
        )
    )

    val jugadorState by jugadorViewModel.state.collectAsState()
    val partidaState by partidaViewModel.state.collectAsState() // ✅ AGREGAR ESTADO DE PARTIDAS

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título principal
        Text(
            text = "🎮 TIC-TAC-TOE",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Sistema de Gestión",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Estadísticas rápidas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📊 Estadísticas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EstadisticaItem(
                        titulo = "Jugadores",
                        valor = "${jugadorState.jugadores.size}",
                        icono = Icons.Default.Person
                    )

                    EstadisticaItem(
                        titulo = "Partidas",
                        valor = "${partidaState.partidas.size}", // ✅ CAMBIO: Usar partidas reales
                        icono = Icons.Default.PlayArrow
                    )
                }
            }
        }

        // Botones de navegación
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MenuButton(
                title = "👥 Registro de Jugadores",
                subtitle = "Crear y gestionar jugadores",
                backgroundColor = Color(0xFF4CAF50),
                onClick = {
                    navController.navigate(AppRoutes.REGISTRO_JUGADORES)
                }
            )

            MenuButton(
                title = "🎯 Registro de Partidas",
                subtitle = "Crear y gestionar partidas",
                backgroundColor = Color(0xFF2196F3),
                onClick = {
                    navController.navigate(AppRoutes.REGISTRO_PARTIDAS)
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Footer
        Text(
            text = "Selecciona una opción para comenzar",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MenuButton(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = backgroundColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun EstadisticaItem(
    titulo: String,
    valor: String,
    icono: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icono,
            contentDescription = titulo,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}