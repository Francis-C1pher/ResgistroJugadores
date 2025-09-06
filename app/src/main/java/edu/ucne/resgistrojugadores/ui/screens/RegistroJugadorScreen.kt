package edu.ucne.RegistroJugadores.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.ucne.RegistroJugadores.Data.DataBase.AppDatabase
import edu.ucne.RegistroJugadores.Data.repository.JugadorRepositoryImpl
import edu.ucne.RegistroJugadores.Domain.model.Jugador
import edu.ucne.RegistroJugadores.Domain.usecase.GetJugadoresUseCase
import edu.ucne.RegistroJugadores.Domain.usecase.InsertJugadorUseCase
import edu.ucne.RegistroJugadores.Domain.usecase.ValidateJugadorUseCase
import edu.ucne.RegistroJugadores.ui.events.RegistroJugadorEvent
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroJugadorViewModel
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroJugadorViewModelFactory

@Composable
fun RegistroJugadorScreen() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { JugadorRepositoryImpl(database.jugadorDao()) }

    val viewModel: RegistroJugadorViewModel = viewModel(
        factory = RegistroJugadorViewModelFactory(
            GetJugadoresUseCase(repository),
            InsertJugadorUseCase(repository),
            ValidateJugadorUseCase(repository)
        )
    )

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro de Jugadores Tic-Tac-Toe",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Campo Nombres
                OutlinedTextField(
                    value = state.nombres,
                    onValueChange = { viewModel.onEvent(RegistroJugadorEvent.NombresChanged(it)) },
                    label = { Text("Nombres *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.nombresError != null,
                    supportingText = {
                        state.nombresError?.let {
                            Text(it, color = Color.Red)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Partidas
                OutlinedTextField(
                    value = state.partidas,
                    onValueChange = { viewModel.onEvent(RegistroJugadorEvent.PartidasChanged(it)) },
                    label = { Text("Partidas *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.partidasError != null,
                    supportingText = {
                        state.partidasError?.let {
                            Text(it, color = Color.Red)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.onEvent(RegistroJugadorEvent.SaveJugador) },
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Guardar")
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.onEvent(RegistroJugadorEvent.ClearForm) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Limpiar")
                    }
                }
            }
        }

        // Mensajes
        state.successMessage?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f))
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Green
                )
            }
        }

        state.errorMessage?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        // Lista de Jugadores
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Jugadores Registrados",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(state.jugadores) { jugador ->
                JugadorItem(jugador = jugador)
            }
        }
    }
}

@Composable
fun JugadorItem(jugador: Jugador) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "ID: ${jugador.jugadorId}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = jugador.nombres,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Partidas: ${jugador.partidas}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}