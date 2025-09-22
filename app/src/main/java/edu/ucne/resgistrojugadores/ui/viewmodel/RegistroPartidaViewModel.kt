package edu.ucne.RegistroJugadores.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.ucne.RegistroJugadores.Domain.exceptions.*
import edu.ucne.RegistroJugadores.Domain.model.Partida
import edu.ucne.RegistroJugadores.Domain.usecase.*
import edu.ucne.RegistroJugadores.ui.events.RegistroPartidaEvent
import edu.ucne.RegistroJugadores.ui.state.RegistroPartidaState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date

class RegistroPartidaViewModel(
    private val getPartidasUseCase: GetPartidasUseCase,
    private val insertPartidaUseCase: InsertPartidaUseCase,
    private val deletePartidaUseCase: DeletePartidaUseCase,
    private val validatePartidaUseCase: ValidatePartidaUseCase,
    private val getJugadoresUseCase: GetJugadoresUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegistroPartidaState())
    val state: StateFlow<RegistroPartidaState> = _state.asStateFlow()

    init {
        getPartidas()
        getJugadores()
    }

    fun onEvent(event: RegistroPartidaEvent) {
        when (event) {
            is RegistroPartidaEvent.FechaChanged -> {
                _state.value = _state.value.copy(
                    fecha = event.fecha,
                    fechaError = null,
                    errorMessage = null
                )
            }

            is RegistroPartidaEvent.Jugador1Changed -> {
                val jugador1Nombre = _state.value.jugadoresDisponibles
                    .find { it.jugadorId == event.jugador1Id }?.nombres ?: ""

                _state.value = _state.value.copy(
                    jugador1Id = event.jugador1Id,
                    jugador1Nombre = jugador1Nombre,
                    jugador1Error = null,
                    errorMessage = null
                )
            }

            is RegistroPartidaEvent.Jugador2Changed -> {
                val jugador2Nombre = _state.value.jugadoresDisponibles
                    .find { it.jugadorId == event.jugador2Id }?.nombres ?: ""

                _state.value = _state.value.copy(
                    jugador2Id = event.jugador2Id,
                    jugador2Nombre = jugador2Nombre,
                    jugador2Error = null,
                    errorMessage = null
                )
            }

            is RegistroPartidaEvent.GanadorChanged -> {
                val ganadorNombre = if (event.ganadorId != null && event.ganadorId > 0) {
                    _state.value.jugadoresDisponibles
                        .find { it.jugadorId == event.ganadorId }?.nombres ?: ""
                } else ""

                _state.value = _state.value.copy(
                    ganadorId = if (event.ganadorId == 0) null else event.ganadorId,
                    ganadorNombre = ganadorNombre,
                    ganadorError = null,
                    errorMessage = null
                )
            }

            is RegistroPartidaEvent.EsFinalizadaChanged -> {
                _state.value = _state.value.copy(
                    esFinalizada = event.esFinalizada,
                    errorMessage = null
                )
            }

            is RegistroPartidaEvent.SavePartida -> {
                savePartida()
            }

            is RegistroPartidaEvent.ClearForm -> {
                _state.value = RegistroPartidaState(
                    partidas = _state.value.partidas,
                    jugadoresDisponibles = _state.value.jugadoresDisponibles
                )
            }

            is RegistroPartidaEvent.DeletePartida -> {
                deletePartida(event.partidaId)
            }

            is RegistroPartidaEvent.SelectPartida -> {
                selectPartida(event.partidaId)
            }

            is RegistroPartidaEvent.LoadJugadores -> {
                getJugadores()
            }
        }
    }

    private fun savePartida() {
        viewModelScope.launch {
            // Validaciones
            val jugador1Error = validatePartidaUseCase.validateJugador1(_state.value.jugador1Id)
            val jugador2Error = validatePartidaUseCase.validateJugador2(_state.value.jugador2Id)
            val jugadoresDiferentesError = validatePartidaUseCase.validateJugadoresDiferentes(
                _state.value.jugador1Id,
                _state.value.jugador2Id
            )
            val ganadorError = validatePartidaUseCase.validateGanador(
                _state.value.ganadorId,
                _state.value.jugador1Id,
                _state.value.jugador2Id
            )
            val fechaError = validatePartidaUseCase.validateFecha(_state.value.fecha)

            if (jugador1Error != null || jugador2Error != null || jugadoresDiferentesError != null ||
                ganadorError != null || fechaError != null) {
                _state.value = _state.value.copy(
                    jugador1Error = jugador1Error,
                    jugador2Error = jugador2Error ?: jugadoresDiferentesError,
                    ganadorError = ganadorError,
                    fechaError = fechaError
                )
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val partida = Partida(
                partidaId = if (_state.value.partidaId == 0) null else _state.value.partidaId,
                fecha = _state.value.fecha,
                jugador1Id = _state.value.jugador1Id,
                jugador2Id = _state.value.jugador2Id,
                ganadorId = _state.value.ganadorId,
                esFinalizada = _state.value.esFinalizada
            )

            insertPartidaUseCase(partida)
                .onSuccess {
                    _state.value = RegistroPartidaState(
                        partidas = _state.value.partidas,
                        jugadoresDisponibles = _state.value.jugadoresDisponibles,
                        successMessage = if (_state.value.partidaId == 0)
                            "Partida creada exitosamente" else "Partida actualizada exitosamente"
                    )
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is PartidaValidationException -> "Error de validación: ${exception.message}"
                        is JugadorInvalidException -> "Error con jugadores: ${exception.message}"
                        is PartidaDatabaseException -> "Error de base de datos: ${exception.message}"
                        is PartidaNotFoundException -> "Partida no encontrada: ${exception.message}"
                        else -> "Error inesperado: ${exception.message ?: "Error desconocido al guardar partida"}"
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
        }
    }

    private fun getPartidas() {
        getPartidasUseCase()
            .onEach { partidas ->
                _state.value = _state.value.copy(partidas = partidas)
            }
            .launchIn(viewModelScope)
    }

    private fun getJugadores() {
        getJugadoresUseCase()
            .onEach { jugadores ->
                _state.value = _state.value.copy(jugadoresDisponibles = jugadores)
            }
            .launchIn(viewModelScope)
    }

    private fun deletePartida(partidaId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            deletePartidaUseCase(partidaId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Partida eliminada exitosamente",
                        partidaId = if (_state.value.partidaId == partidaId) 0 else _state.value.partidaId,
                        jugador1Id = if (_state.value.partidaId == partidaId) 0 else _state.value.jugador1Id,
                        jugador2Id = if (_state.value.partidaId == partidaId) 0 else _state.value.jugador2Id,
                        ganadorId = if (_state.value.partidaId == partidaId) null else _state.value.ganadorId,
                        esFinalizada = if (_state.value.partidaId == partidaId) false else _state.value.esFinalizada
                    )
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is PartidaNotFoundException -> "No se puede eliminar: ${exception.message}"
                        is PartidaDatabaseException -> "Error al eliminar: ${exception.message}"
                        else -> "Error inesperado al eliminar: ${exception.message ?: "Error desconocido"}"
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
        }
    }

    private fun selectPartida(partidaId: Int) {
        viewModelScope.launch {
            try {
                val partida = _state.value.partidas.find { it.partidaId == partidaId }

                if (partida == null) {
                    _state.value = _state.value.copy(
                        errorMessage = "No se encontró la partida con ID: $partidaId"
                    )
                    return@launch
                }

                // Obtener nombres de jugadores
                val jugador1Nombre = _state.value.jugadoresDisponibles
                    .find { it.jugadorId == partida.jugador1Id }?.nombres ?: ""
                val jugador2Nombre = _state.value.jugadoresDisponibles
                    .find { it.jugadorId == partida.jugador2Id }?.nombres ?: ""
                val ganadorNombre = if (partida.ganadorId != null) {
                    _state.value.jugadoresDisponibles
                        .find { it.jugadorId == partida.ganadorId }?.nombres ?: ""
                } else ""

                _state.value = _state.value.copy(
                    partidaId = partida.partidaId ?: 0,
                    fecha = partida.fecha,
                    jugador1Id = partida.jugador1Id,
                    jugador2Id = partida.jugador2Id,
                    ganadorId = partida.ganadorId,
                    esFinalizada = partida.esFinalizada,
                    jugador1Nombre = jugador1Nombre,
                    jugador2Nombre = jugador2Nombre,
                    ganadorNombre = ganadorNombre,
                    errorMessage = null,
                    jugador1Error = null,
                    jugador2Error = null,
                    ganadorError = null,
                    fechaError = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Error al seleccionar partida: ${e.message}"
                )
            }
        }
    }
}

class RegistroPartidaViewModelFactory(
    private val getPartidasUseCase: GetPartidasUseCase,
    private val insertPartidaUseCase: InsertPartidaUseCase,
    private val deletePartidaUseCase: DeletePartidaUseCase,
    private val validatePartidaUseCase: ValidatePartidaUseCase,
    private val getJugadoresUseCase: GetJugadoresUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroPartidaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroPartidaViewModel(
                getPartidasUseCase,
                insertPartidaUseCase,
                deletePartidaUseCase,
                validatePartidaUseCase,
                getJugadoresUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}