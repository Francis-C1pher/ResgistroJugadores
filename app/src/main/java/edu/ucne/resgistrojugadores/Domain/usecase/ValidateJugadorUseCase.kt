package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository

class ValidateJugadorUseCase(
    private val repository: JugadorRepository
) {
    suspend fun validateNombre(nombre: String, jugadorId: Int = 0): String? {
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            nombre.trim().length < 2 -> "El nombre debe tener al menos 2 caracteres"
            nombre.trim().length > 50 -> "El nombre no puede tener más de 50 caracteres"
            else -> {
                // ✅ CAMBIO: Solo validar nombre único si es un nuevo jugador O si es un jugador diferente
                if (jugadorId == 0) {
                    // Nuevo jugador: validar que no existe
                    if (repository.existeNombre(nombre.trim())) {
                        "Ya existe un jugador con ese nombre"
                    } else null
                } else {
                    // Jugador existente: validar solo si el nombre cambió
                    val jugadorActual = repository.getJugadorById(jugadorId)
                    if (jugadorActual != null &&
                        jugadorActual.nombres.trim() != nombre.trim() &&
                        repository.existeNombre(nombre.trim())) {
                        "Ya existe un jugador con ese nombre"
                    } else null
                }
            }
        }
    }

    suspend fun validatePartidas(partidas: String): String? {
        return when {
            partidas.isBlank() -> "Las partidas son obligatorias"
            else -> {
                try {
                    val partidasInt = partidas.toInt()
                    if (partidasInt < 0) "Las partidas no pueden ser negativas"
                    else null
                } catch (e: NumberFormatException) {
                    "Las partidas deben ser un número válido"
                }
            }
        }
    }
}