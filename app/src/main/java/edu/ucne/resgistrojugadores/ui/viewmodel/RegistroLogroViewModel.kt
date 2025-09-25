package edu.ucne.RegistroJugadores.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.ucne.RegistroJugadores.Domain.exceptions.*
import edu.ucne.RegistroJugadores.Domain.model.Logro
import edu.ucne.RegistroJugadores.Domain.usecase.*
import edu.ucne.RegistroJugadores.ui.events.RegistroLogroEvent
import edu.ucne.RegistroJugadores.ui.state.RegistroLogroState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RegistroLogroViewModel(
    private val getLogrosUseCase: GetLogrosUseCase,
    private val insertLogroUseCase: InsertLogroUseCase,
    private val deleteLogroUseCase: DeleteLogroUseCase,
    private val validateLogroUseCase: ValidateLogroUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegistroLogroState())
    val state: StateFlow<RegistroLogroState> = _state.asStateFlow()

    init {
        getLogros()
    }

    fun onEvent(event: RegistroLogroEvent) {
        when (event) {
            is RegistroLogroEvent.NombreChanged -> {
                _state.value = _state.value.copy(
                    nombre = event.nombre,
                    nombreError = null,
                    errorMessage = null
                )
            }

            is RegistroLogroEvent.DescripcionChanged -> {
                _state.value = _state.value.copy(
                    descripcion = event.descripcion,
                    descripcionError = null,
                    errorMessage = null
                )
            }

            is RegistroLogroEvent.SaveLogro -> {
                saveLogro()
            }

            is RegistroLogroEvent.ClearForm -> {
                _state.value = RegistroLogroState(logros = _state.value.logros)
            }

            is RegistroLogroEvent.DeleteLogro -> {
                deleteLogro(event.logroId)
            }

            is RegistroLogroEvent.SelectLogro -> {
                selectLogro(event.logroId)
            }
        }
    }

    private fun saveLogro() {
        viewModelScope.launch {
            // Validaciones
            val nombreError = validateLogroUseCase.validateNombre(_state.value.nombre)
            val descripcionError = validateLogroUseCase.validateDescripcion(_state.value.descripcion)

            if (nombreError != null || descripcionError != null) {
                _state.value = _state.value.copy(
                    nombreError = nombreError,
                    descripcionError = descripcionError
                )
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val logro = Logro(
                logroId = if (_state.value.logroId == 0) null else _state.value.logroId,
                nombre = _state.value.nombre.trim(),
                descripcion = _state.value.descripcion.trim()
            )

            insertLogroUseCase(logro)
                .onSuccess {
                    _state.value = RegistroLogroState(
                        logros = _state.value.logros,
                        successMessage = if (_state.value.logroId == 0)
                            "Logro creado exitosamente" else "Logro actualizado exitosamente"
                    )
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is LogroValidationException -> "Error de validación: ${exception.message}"
                        is LogroAlreadyExistsException -> "Logro duplicado: ${exception.message}"
                        is LogroDatabaseException -> "Error de base de datos: ${exception.message}"
                        is LogroNotFoundException -> "Logro no encontrado: ${exception.message}"
                        else -> "Error inesperado: ${exception.message ?: "Error desconocido al guardar logro"}"
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
        }
    }

    private fun getLogros() {
        getLogrosUseCase()
            .onEach { logros ->
                _state.value = _state.value.copy(logros = logros)
            }
            .launchIn(viewModelScope)
    }

    private fun deleteLogro(logroId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            deleteLogroUseCase(logroId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Logro eliminado exitosamente",
                        logroId = if (_state.value.logroId == logroId) 0 else _state.value.logroId,
                        nombre = if (_state.value.logroId == logroId) "" else _state.value.nombre,
                        descripcion = if (_state.value.logroId == logroId) "" else _state.value.descripcion
                    )
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is LogroNotFoundException -> "No se puede eliminar: ${exception.message}"
                        is LogroDatabaseException -> "Error al eliminar: ${exception.message}"
                        else -> "Error inesperado al eliminar: ${exception.message ?: "Error desconocido"}"
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
        }
    }

    private fun selectLogro(logroId: Int) {
        viewModelScope.launch {
            try {
                val logro = _state.value.logros.find { it.logroId == logroId }

                if (logro == null) {
                    _state.value = _state.value.copy(
                        errorMessage = "No se encontró el logro con ID: $logroId"
                    )
                    return@launch
                }

                _state.value = _state.value.copy(
                    logroId = logro.logroId ?: 0,
                    nombre = logro.nombre,
                    descripcion = logro.descripcion,
                    errorMessage = null,
                    nombreError = null,
                    descripcionError = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Error al seleccionar logro: ${e.message}"
                )
            }
        }
    }
}

class RegistroLogroViewModelFactory(
    private val getLogrosUseCase: GetLogrosUseCase,
    private val insertLogroUseCase: InsertLogroUseCase,
    private val deleteLogroUseCase: DeleteLogroUseCase,
    private val validateLogroUseCase: ValidateLogroUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroLogroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroLogroViewModel(
                getLogrosUseCase,
                insertLogroUseCase,
                deleteLogroUseCase,
                validateLogroUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}