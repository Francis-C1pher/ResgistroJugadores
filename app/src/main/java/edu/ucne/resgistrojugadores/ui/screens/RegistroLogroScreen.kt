package edu.ucne.RegistroJugadores.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.ucne.RegistroJugadores.Domain.model.Logro
import edu.ucne.RegistroJugadores.JugadorApplication
import edu.ucne.RegistroJugadores.ui.events.RegistroLogroEvent
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroLogroViewModel
import edu.ucne.RegistroJugadores.ui.viewmodel.RegistroLogroViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroLogroScreen(onNavigateBack: (() -> Unit)? = null) {

    val context = LocalContext.current
    val application = context.applicationContext as JugadorApplication

    val viewModel: RegistroLogroViewModel = viewModel(
        factory = RegistroLogroViewModelFactory(
            application.getLogrosUseCase,
            application.insertLogroUseCase,
            application.deleteLogroUseCase,
            application.validateLogroUseCase
        )
    )

    val state by viewModel.state.collectAsState()

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
            text = "Registro de Logros Tic-Tac-Toe",
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
                // Campo Nombre
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { viewModel.onEvent(RegistroLogroEvent.NombreChanged(it)) },
                    label = { Text("Nombre del Logro *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.nombreError != null,
                    supportingText = {
                        state.nombreError?.let {
                            Text(it, color = Color.Red)
                        }
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = "Logro",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Descripción
                OutlinedTextField(
                    value = state.descripcion,
                    onValueChange = { viewModel.onEvent(RegistroLogroEvent.DescripcionChanged(it)) },
                    label = { Text("Descripción *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.descripcionError != null,
                    supportingText = {
                        state.descripcionError?.let {
                            Text(it, color = Color.Red)
                        }
                    },
                    maxLines = 4,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.onEvent(RegistroLogroEvent.SaveLogro) },
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White
                            )
                        } else {
                            Text(if (state.logroId == 0) "Crear Logro" else "Actualizar Logro")
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.onEvent(RegistroLogroEvent.ClearForm) },
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

        // Lista de Logros
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Logros Registrados",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(state.logros) { logro ->
                LogroItem(
                    logro = logro,
                    onEdit = { viewModel.onEvent(RegistroLogroEvent.SelectLogro(it.logroId ?: 0)) },
                    onDelete = { viewModel.onEvent(RegistroLogroEvent.DeleteLogro(it.logroId ?: 0)) }
                )
            }
        }
    }
}

@Composable
fun LogroItem(
    logro: Logro,
    onEdit: (Logro) -> Unit,
    onDelete: (Logro) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = "Logro",
                    tint = Color(0xFFFFD700), // Dorado
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 12.dp)
                )

                Column {
                    Text(
                        text = "ID: ${logro.logroId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = logro.nombre,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = logro.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
            }

            // Botones de Acción
            Row {
                IconButton(onClick = { onEdit(logro) }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar logro",
                        tint = Color.Blue
                    )
                }
                IconButton(onClick = { onDelete(logro) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar logro",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}