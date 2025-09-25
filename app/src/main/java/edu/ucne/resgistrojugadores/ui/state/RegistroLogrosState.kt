package edu.ucne.RegistroJugadores.ui.state

import edu.ucne.RegistroJugadores.Domain.model.Logro

data class RegistroLogroState(
    val logroId: Int = 0,
    val nombre: String = "",
    val descripcion: String = "",

    // Errores de validación
    val nombreError: String? = null,
    val descripcionError: String? = null,

    // Estados generales
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,

    // Lista de logros
    val logros: List<Logro> = emptyList()
)