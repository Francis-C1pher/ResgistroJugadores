package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.exceptions.*
import edu.ucne.RegistroJugadores.Domain.model.Partida
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository
import edu.ucne.RegistroJugadores.Domain.repository.PartidaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

// ======================== GET PARTIDAS USE CASE ========================
class GetPartidasUseCase(
    private val repository: PartidaRepository
) {
    operator fun invoke(): Flow<List<Partida>> {
        return repository.getAllPartidas()
    }
}