package edu.ucne.RegistroJugadores.Domain.usecase


import edu.ucne.RegistroJugadores.Domain.model.Jugador
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository

class InsertJugadorUseCase(
    private val repository: JugadorRepository
) {
    suspend operator fun invoke(jugador: Jugador): Result<Long> {
        return try {
            // Validar que el nombre no esté vacío
            if (jugador.nombres.isBlank()) {
                return Result.failure(Exception("El nombre es obligatorio"))
            }

            // Validar que las partidas no sean negativas
            if (jugador.partidas < 0) {
                return Result.failure(Exception("Las partidas no pueden ser negativas"))
            }

            // Validar que no exista el nombre
            if (repository.existeNombre(jugador.nombres.trim())) {
                return Result.failure(Exception("Ya existe un jugador con ese nombre"))
            }

            val id = repository.insertJugador(jugador.copy(nombres = jugador.nombres.trim()))
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}