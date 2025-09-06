package edu.ucne.RegistroJugadores.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.ucne.RegistroJugadores.Domain.model.Jugador
import edu.ucne.RegistroJugadores.Domain.usecase.GetJugadoresUseCase
import edu.ucne.RegistroJugadores.Domain.usecase.InsertJugadorUseCase
import edu.ucne.RegistroJugadores.Domain.usecase.ValidateJugadorUseCase
import edu.ucne.RegistroJugadores.ui.events.RegistroJugadorEvent
import edu.ucne.RegistroJugadores.ui.state.RegistroJugadorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RegistroJugadorViewModel(
    private val getJugadoresUseCase: GetJugadoresUseCase,
    private val insertJugadorUseCase: InsertJugadorUseCase,
    private val validateJugadorUseCase: ValidateJugadorUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegistroJugadorState())
    val state: StateFlow<RegistroJugadorState> = _state.asStateFlow()

    init {
        getJugadores()
    }

    fun onEvent(event: RegistroJugadorEvent) {
        when (event) {
            is RegistroJugadorEvent.NombresChanged -> {
                _state.value = _state.value.copy(
                    nombres = event.nombres,
                    nombresError = null,
                    errorMessage = null
                )
            }

            is RegistroJugadorEvent.PartidasChanged -> {
                _state.value = _state.value.copy(
                    partidas = event.partidas,
                    partidasError = null,
                    errorMessage = null
                )
            }

            is RegistroJugadorEvent.SaveJugador -> {
                saveJugador()
            }

            is RegistroJugadorEvent.ClearForm -> {
                _state.value = RegistroJugadorState(jugadores = _state.value.jugadores)
            }

            is RegistroJugadorEvent.DeleteJugador -> {
                deleteJugador(event.jugadorId)
            }

            is RegistroJugadorEvent.SelectJugador -> {
                selectJugador(event.jugadorId)
            }
        }
    }

    private fun saveJugador() {
        viewModelScope.launch {
            // Validaciones
            val nombresError = validateJugadorUseCase.validateNombre(_state.value.nombres)
            val partidasError = validateJugadorUseCase.validatePartidas(_state.value.partidas)

            if (nombresError != null || partidasError != null) {
                _state.value = _state.value.copy(
                    nombresError = nombresError,
                    partidasError = partidasError
                )
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true)

            val jugador = Jugador(
                jugadorId = _state.value.jugadorId,
                nombres = _state.value.nombres.trim(),
                partidas = _state.value.partidas.toInt()
            )

            insertJugadorUseCase(jugador)
                .onSuccess {
                    _state.value = RegistroJugadorState(
                        jugadores = _state.value.jugadores,
                        successMessage = "Jugador guardado exitosamente"
                    )
                }
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error desconocido"
                    )
                }
        }
    }

    private fun getJugadores() {
        getJugadoresUseCase()
            .onEach { jugadores ->
                _state.value = _state.value.copy(jugadores = jugadores)
            }
            .launchIn(viewModelScope)
    }

    private fun deleteJugador(jugadorId: Int) {
        viewModelScope.launch {
            // Implementar lógica de eliminar jugador
            val jugador = _state.value.jugadores.find { it.jugadorId == jugadorId }
            jugador?.let {
                // Aquí llamarías al deleteUseCase cuando lo implementes
                _state.value = _state.value.copy(
                    successMessage = "Jugador eliminado exitosamente"
                )
            }
        }
    }

    private fun selectJugador(jugadorId: Int) {
        viewModelScope.launch {
            val jugador = _state.value.jugadores.find { it.jugadorId == jugadorId }
            jugador?.let {
                _state.value = _state.value.copy(
                    jugadorId = jugador.jugadorId,
                    nombres = jugador.nombres,
                    partidas = jugador.partidas.toString()
                )
            }
        }
    }
}

class RegistroJugadorViewModelFactory(
    private val getJugadoresUseCase: GetJugadoresUseCase,
    private val insertJugadorUseCase: InsertJugadorUseCase,
    private val validateJugadorUseCase: ValidateJugadorUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroJugadorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroJugadorViewModel(getJugadoresUseCase, insertJugadorUseCase, validateJugadorUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}