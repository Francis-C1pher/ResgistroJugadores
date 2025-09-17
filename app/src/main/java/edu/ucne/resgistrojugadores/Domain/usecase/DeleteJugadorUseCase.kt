package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.exceptions.JugadorNotFoundException
import edu.ucne.RegistroJugadores.Domain.model.Jugador
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository

class DeleteJugadorUseCase(
    private val repository: JugadorRepository
) {
    suspend operator fun invoke(jugador: Jugador): Result<Unit> {
        return try {
            // Validar que el jugador exista
            val existingJugador = repository.getJugadorById(jugador.jugadorId ?: 0)
                ?: return Result.failure(JugadorNotFoundException("No se encontró el jugador con ID ${jugador.jugadorId}"))

            repository.deleteJugador(existingJugador)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend operator fun invoke(jugadorId: Int): Result<Unit> {
        return try {
            // Validar que el jugador exista
            val existingJugador = repository.getJugadorById(jugadorId)
                ?: return Result.failure(JugadorNotFoundException("No se encontró el jugador con ID $jugadorId"))

            repository.deleteJugador(existingJugador)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}