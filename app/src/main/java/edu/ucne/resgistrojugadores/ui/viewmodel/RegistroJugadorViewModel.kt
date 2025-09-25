package edu.ucne.RegistroJugadores.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.ucne.RegistroJugadores.Domain.exceptions.JugadorAlreadyExistsException
import edu.ucne.RegistroJugadores.Domain.exceptions.JugadorNotFoundException
import edu.ucne.RegistroJugadores.Domain.exceptions.JugadorValidationException
import edu.ucne.RegistroJugadores.Domain.exceptions.InvalidPartidasException
import edu.ucne.RegistroJugadores.Domain.exceptions.JugadorDatabaseException
import edu.ucne.RegistroJugadores.Domain.model.Jugador
import edu.ucne.RegistroJugadores.Domain.usecase.DeleteJugadorUseCase
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
    private val deleteJugadorUseCase: DeleteJugadorUseCase, // ✅ Nueva dependencia
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
            // Validación de que partidas sea número entero
            val partidasInt = try {
                if (_state.value.partidas.isBlank()) {
                    _state.value = _state.value.copy(partidasError = "Las partidas son obligatorias")
                    return@launch
                }
                _state.value.partidas.toInt()
            } catch (e: NumberFormatException) {
                _state.value = _state.value.copy(
                    partidasError = "Las partidas deben ser un número entero válido"
                )
                return@launch
            }

            // ✅ CAMBIO: Pasar jugadorId a las validaciones
            val nombresError = validateJugadorUseCase.validateNombre(_state.value.nombres, _state.value.jugadorId)
            val partidasError = validateJugadorUseCase.validatePartidas(_state.value.partidas)

            if (nombresError != null || partidasError != null) {
                _state.value = _state.value.copy(
                    nombresError = nombresError,
                    partidasError = partidasError
                )
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val jugador = Jugador(
                jugadorId = if (_state.value.jugadorId == 0) null else _state.value.jugadorId,
                nombres = _state.value.nombres.trim(),
                partidas = partidasInt
            )

            insertJugadorUseCase(jugador)
                .onSuccess {
                    _state.value = RegistroJugadorState(
                        jugadores = _state.value.jugadores,
                        successMessage = if (_state.value.jugadorId == 0)
                            "Jugador creado exitosamente" else "Jugador actualizado exitosamente"
                    )
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is JugadorValidationException -> "Error de validación: ${exception.message}"
                        is JugadorAlreadyExistsException -> "Jugador duplicado: ${exception.message}"
                        is InvalidPartidasException -> "Error en partidas: ${exception.message}"
                        is JugadorDatabaseException -> "Error de base de datos: ${exception.message}"
                        is JugadorNotFoundException -> "Jugador no encontrado: ${exception.message}"
                        else -> "Error inesperado: ${exception.message ?: "Error desconocido al guardar jugador"}"
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
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

    // ✅ ISSUE 1 SOLUCIONADO: Función deleteJugador completa con llamada al UseCase
    private fun deleteJugador(jugadorId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            deleteJugadorUseCase(jugadorId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Jugador eliminado exitosamente",
                        // Limpiar formulario si el jugador eliminado estaba seleccionado
                        jugadorId = if (_state.value.jugadorId == jugadorId) 0 else _state.value.jugadorId,
                        nombres = if (_state.value.jugadorId == jugadorId) "" else _state.value.nombres,
                        partidas = if (_state.value.jugadorId == jugadorId) "" else _state.value.partidas
                    )
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is JugadorNotFoundException -> "No se puede eliminar: ${exception.message}"
                        is JugadorDatabaseException -> "Error al eliminar: ${exception.message}"
                        else -> "Error inesperado al eliminar: ${exception.message ?: "Error desconocido"}"
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
        }
    }

    // ✅ ISSUE 3 SOLUCIONADO: selectJugador maneja el caso donde no se encuentra el jugador
    private fun selectJugador(jugadorId: Int) {
        viewModelScope.launch {
            try {
                val jugador = _state.value.jugadores.find { it.jugadorId == jugadorId }

                if (jugador == null) {
                    // Manejo del caso donde no se encuentra el jugador
                    _state.value = _state.value.copy(
                        errorMessage = "No se encontró el jugador con ID: $jugadorId"
                    )
                    return@launch
                }

                // Jugador encontrado, actualizar el estado
                _state.value = _state.value.copy(
                    jugadorId = jugador.jugadorId ?: 0,
                    nombres = jugador.nombres,
                    partidas = jugador.partidas.toString(),
                    errorMessage = null, // Limpiar mensajes de error previos
                    nombresError = null,
                    partidasError = null
                )

            } catch (e: Exception) {
                // Manejo de cualquier excepción inesperada
                _state.value = _state.value.copy(
                    errorMessage = "Error al seleccionar jugador: ${e.message}"
                )
            }
        }
    }
}

// ✅ ACTUALIZAR FACTORY: Agregar DeleteJugadorUseCase
class RegistroJugadorViewModelFactory(
    private val getJugadoresUseCase: GetJugadoresUseCase,
    private val insertJugadorUseCase: InsertJugadorUseCase,
    private val deleteJugadorUseCase: DeleteJugadorUseCase, // ✅ Nueva dependencia
    private val validateJugadorUseCase: ValidateJugadorUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroJugadorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroJugadorViewModel(
                getJugadoresUseCase,
                insertJugadorUseCase,
                deleteJugadorUseCase, // ✅ Pasar nueva dependencia
                validateJugadorUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}