package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.exceptions.*
import edu.ucne.RegistroJugadores.Domain.model.Partida
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository
import edu.ucne.RegistroJugadores.Domain.repository.PartidaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

class DeletePartidaUseCase(
    private val repository: PartidaRepository
) {
    suspend operator fun invoke(partida: Partida): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val existingPartida = repository.getPartidaById(partida.partidaId ?: 0)
                    ?: return@withContext Result.failure(
                        PartidaNotFoundException("No se encontró la partida con ID ${partida.partidaId}")
                    )

                repository.deletePartida(existingPartida)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(PartidaDatabaseException("Error al eliminar partida: ${e.message}"))
        }
    }

    suspend operator fun invoke(partidaId: Int): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val existingPartida = repository.getPartidaById(partidaId)
                    ?: return@withContext Result.failure(
                        PartidaNotFoundException("No se encontró la partida con ID $partidaId")
                    )

                repository.deletePartida(existingPartida)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(PartidaDatabaseException("Error al eliminar partida: ${e.message}"))
        }
    }
}