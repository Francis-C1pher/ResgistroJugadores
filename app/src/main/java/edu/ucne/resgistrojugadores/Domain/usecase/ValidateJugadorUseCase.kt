package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository

class ValidateJugadorUseCase(
    private val repository: JugadorRepository
) {
    suspend fun validateNombre(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            repository.existeNombre(nombre.trim()) -> "Ya existe un jugador con ese nombre"
            else -> null
        }
    }

    fun validatePartidas(partidas: String): String? {
        return when {
            partidas.isBlank() -> "Las partidas son obligatorias"
            partidas.toIntOrNull() == null -> "Las partidas deben ser un número válido"
            partidas.toInt() < 0 -> "Las partidas no pueden ser negativas"
            else -> null
        }
    }
}