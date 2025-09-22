package edu.ucne.RegistroJugadores.Domain.repository

import edu.ucne.RegistroJugadores.Domain.model.Partida
import kotlinx.coroutines.flow.Flow

interface PartidaRepository {
    suspend fun insertPartida(partida: Partida): Long
    suspend fun updatePartida(partida: Partida)
    suspend fun deletePartida(partida: Partida)
    fun getAllPartidas(): Flow<List<Partida>>
    suspend fun getPartidaById(id: Int): Partida?
    fun getPartidasByJugador(jugadorId: Int): Flow<List<Partida>>
    suspend fun getPartidasCountByJugador(jugadorId: Int): Int
}