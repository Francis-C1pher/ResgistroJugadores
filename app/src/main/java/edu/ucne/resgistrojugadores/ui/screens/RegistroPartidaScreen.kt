package edu.ucne.RegistroJugadores.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.ucne.RegistroJugadores.Domain.model.Jugador
import edu.ucne.RegistroJugadores.Domain.model.Partida
import edu.ucne.RegistroJugadores.JugadorApplication
import edu.ucne.RegistroJugadores.ui.events.RegistroPartidaEvent
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroPartidaViewModel
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroPartidaViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroPartidaScreen(onNavigateBack: (() -> Unit)? = null) {

    val context = LocalContext.current
    val application = context.applicationContext as JugadorApplication

    val viewModel: RegistroPartidaViewModel = viewModel(
        factory = RegistroPartidaViewModelFactory(
            application.getPartidasUseCase,
            application.insertPartidaUseCase,
            application.deletePartidaUseCase,
            application.validatePartidaUseCase,
            application.getJugadoresUseCase
        )
    )

    val state by viewModel.state.collectAsState()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón Volver al Menú
        onNavigateBack?.let { navigateBack ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                OutlinedButton(
                    onClick = navigateBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Volver al Menú")
                }
            }
        }

        Text(
            text = "Registro de Partidas Tic-Tac-Toe",
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
                // Selector de Fecha
                Text(
                    text = "Fecha de la Partida",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        calendar.time = state.fecha
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val newDate = Calendar.getInstance().apply {
                                    set(year, month, dayOfMonth)
                                }.time
                                viewModel.onEvent(RegistroPartidaEvent.FechaChanged(newDate))
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(dateFormat.format(state.fecha))
                }

                state.fechaError?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector Jugador 1
                Text(
                    text = "Jugador 1",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                JugadorDropdown(
                    label = "Seleccionar Jugador 1",
                    jugadores = state.jugadoresDisponibles,
                    selectedJugadorId = state.jugador1Id,
                    onJugadorSelected = { viewModel.onEvent(RegistroPartidaEvent.Jugador1Changed(it)) },
                    isError = state.jugador1Error != null
                )

                state.jugador1Error?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector Jugador 2
                Text(
                    text = "Jugador 2",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                JugadorDropdown(
                    label = "Seleccionar Jugador 2",
                    jugadores = state.jugadoresDisponibles,
                    selectedJugadorId = state.jugador2Id,
                    onJugadorSelected = { viewModel.onEvent(RegistroPartidaEvent.Jugador2Changed(it)) },
                    isError = state.jugador2Error != null
                )

                state.jugador2Error?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector Ganador
                Text(
                    text = "Ganador (Opcional)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                GanadorDropdown(
                    jugador1Id = state.jugador1Id,
                    jugador2Id = state.jugador2Id,
                    jugadores = state.jugadoresDisponibles,
                    selectedGanadorId = state.ganadorId,
                    onGanadorSelected = { viewModel.onEvent(RegistroPartidaEvent.GanadorChanged(it)) },
                    isError = state.ganadorError != null
                )

                state.ganadorError?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox Finalizada
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.esFinalizada,
                        onCheckedChange = { viewModel.onEvent(RegistroPartidaEvent.EsFinalizadaChanged(it)) }
                    )
                    Text("Partida Finalizada")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.onEvent(RegistroPartidaEvent.SavePartida) },
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White
                            )
                        } else {
                            Text(if (state.partidaId == 0) "Crear Partida" else "Actualizar Partida")
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.onEvent(RegistroPartidaEvent.ClearForm) },
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

        // Lista de Partidas
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Partidas Registradas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(state.partidas) { partida ->
                PartidaItem(
                    partida = partida,
                    jugadores = state.jugadoresDisponibles,
                    onEdit = { viewModel.onEvent(RegistroPartidaEvent.SelectPartida(it.partidaId ?: 0)) },
                    onDelete = { viewModel.onEvent(RegistroPartidaEvent.DeletePartida(it.partidaId ?: 0)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorDropdown(
    label: String,
    jugadores: List<Jugador>,
    selectedJugadorId: Int,
    onJugadorSelected: (Int) -> Unit,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedJugador = jugadores.find { it.jugadorId == selectedJugadorId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedJugador?.nombres ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            isError = isError
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            jugadores.forEach { jugador ->
                DropdownMenuItem(
                    text = { Text("${jugador.nombres} (${jugador.partidas} partidas)") },
                    onClick = {
                        onJugadorSelected(jugador.jugadorId ?: 0)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GanadorDropdown(
    jugador1Id: Int,
    jugador2Id: Int,
    jugadores: List<Jugador>,
    selectedGanadorId: Int?,
    onGanadorSelected: (Int?) -> Unit,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val jugadoresPartida = jugadores.filter {
        it.jugadorId == jugador1Id || it.jugadorId == jugador2Id
    }
    val selectedGanador = jugadores.find { it.jugadorId == selectedGanadorId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedGanador?.nombres ?: "Sin ganador / Empate",
            onValueChange = {},
            readOnly = true,
            label = { Text("Ganador") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            isError = isError
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin ganador / Empate") },
                onClick = {
                    onGanadorSelected(null)
                    expanded = false
                }
            )

            jugadoresPartida.forEach { jugador ->
                DropdownMenuItem(
                    text = { Text(jugador.nombres) },
                    onClick = {
                        onGanadorSelected(jugador.jugadorId ?: 0)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PartidaItem(
    partida: Partida,
    jugadores: List<Jugador>,
    onEdit: (Partida) -> Unit,
    onDelete: (Partida) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val jugador1 = jugadores.find { it.jugadorId == partida.jugador1Id }
    val jugador2 = jugadores.find { it.jugadorId == partida.jugador2Id }
    val ganador = if (partida.ganadorId != null) {
        jugadores.find { it.jugadorId == partida.ganadorId }
    } else null

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Partida #${partida.partidaId}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Text(
                        text = "📅 ${dateFormat.format(partida.fecha)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Jugadores
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Jugador 1",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = jugador1?.nombres ?: "Desconocido",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Jugador 2",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = jugador2?.nombres ?: "Desconocido",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Estado y Ganador
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Estado
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (partida.esFinalizada) Color.Green.copy(alpha = 0.2f)
                                else Color(0xFFFFA500).copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = if (partida.esFinalizada) "✅ Finalizada" else "⏳ En curso",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (partida.esFinalizada) Color.Green else Color(0xFFFFA500),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Ganador
                        if (partida.esFinalizada) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Blue.copy(alpha = 0.2f)
                                )
                            ) {
                                Text(
                                    text = if (ganador != null) "🏆 ${ganador.nombres}" else "🤝 Empate",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Botones de Acción
                Column {
                    IconButton(onClick = { onEdit(partida) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar partida",
                            tint = Color.Blue
                        )
                    }
                    IconButton(onClick = { onDelete(partida) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar partida",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}