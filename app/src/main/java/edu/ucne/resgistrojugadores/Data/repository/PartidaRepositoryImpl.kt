package edu.ucne.RegistroJugadores.Data.repository

import edu.ucne.RegistroJugadores.Data.Dao.PartidaDao
import edu.ucne.RegistroJugadores.Data.mappers.toDomain
import edu.ucne.RegistroJugadores.Data.mappers.toEntity
import edu.ucne.RegistroJugadores.Domain.model.Partida
import edu.ucne.RegistroJugadores.Domain.repository.PartidaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PartidaRepositoryImpl(
    private val partidaDao: PartidaDao
) : PartidaRepository {

    override suspend fun insertPartida(partida: Partida): Long {
        return partidaDao.insert(partida.toEntity())
    }

    override suspend fun updatePartida(partida: Partida) {
        partidaDao.update(partida.toEntity())
    }

    override suspend fun deletePartida(partida: Partida) {
        partidaDao.delete(partida.toEntity())
    }

    override fun getAllPartidas(): Flow<List<Partida>> {
        return partidaDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPartidaById(id: Int): Partida? {
        return partidaDao.getById(id)?.toDomain()
    }

    override fun getPartidasByJugador(jugadorId: Int): Flow<List<Partida>> {
        return partidaDao.getByJugador(jugadorId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPartidasCountByJugador(jugadorId: Int): Int {
        return partidaDao.getPartidasCountByJugador(jugadorId)
    }
}