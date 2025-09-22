
package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.exceptions.*
import edu.ucne.RegistroJugadores.Domain.model.Partida
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository
import edu.ucne.RegistroJugadores.Domain.repository.PartidaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date


class InsertPartidaUseCase(
    private val partidaRepository: PartidaRepository,
    private val jugadorRepository: JugadorRepository
) {
    suspend operator fun invoke(partida: Partida): Result<Long> {
        return try {
            withContext(Dispatchers.IO) {
                // Validar que los jugadores existan
                val jugador1 = jugadorRepository.getJugadorById(partida.jugador1Id)
                    ?: return@withContext Result.failure(
                        JugadorInvalidException("No existe el jugador 1 con ID: ${partida.jugador1Id}")
                    )

                val jugador2 = jugadorRepository.getJugadorById(partida.jugador2Id)
                    ?: return@withContext Result.failure(
                        JugadorInvalidException("No existe el jugador 2 con ID: ${partida.jugador2Id}")
                    )

                // Validar que no sean el mismo jugador
                if (partida.jugador1Id == partida.jugador2Id) {
                    return@withContext Result.failure(
                        PartidaValidationException("Un jugador no puede jugar contra sí mismo")
                    )
                }

                // Validar ganador si existe
                if (partida.ganadorId != null) {
                    if (partida.ganadorId != partida.jugador1Id && partida.ganadorId != partida.jugador2Id) {
                        return@withContext Result.failure(
                            PartidaValidationException("El ganador debe ser uno de los jugadores de la partida")
                        )
                    }
                }

                // Validar fecha no futuro
                if (partida.fecha.after(Date())) {
                    return@withContext Result.failure(
                        PartidaValidationException("La fecha de la partida no puede ser futura")
                    )
                }

                try {
                    val id = partidaRepository.insertPartida(partida)
                    Result.success(id)
                } catch (e: Exception) {
                    Result.failure(PartidaDatabaseException("Error al guardar partida: ${e.message}"))
                }
            }
        } catch (e: PartidaException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(PartidaDatabaseException("Error inesperado: ${e.message}"))
        }
    }
}
