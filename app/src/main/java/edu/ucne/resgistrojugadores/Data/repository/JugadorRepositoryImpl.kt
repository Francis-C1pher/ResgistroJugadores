package edu.ucne.RegistroJugadores.Data.repository


import edu.ucne.RegistroJugadores.Data.Dao.JugadorDao
import edu.ucne.RegistroJugadores.Data.mappers.toDomain
import edu.ucne.RegistroJugadores.Data.mappers.toEntity
import edu.ucne.RegistroJugadores.Domain.model.Jugador
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JugadorRepositoryImpl(
    private val jugadorDao: JugadorDao
) : JugadorRepository {

    override suspend fun insertJugador(jugador: Jugador): Long {
        return jugadorDao.insert(jugador.toEntity())
    }

    override suspend fun updateJugador(jugador: Jugador) {
        jugadorDao.update(jugador.toEntity())
    }

    override suspend fun deleteJugador(jugador: Jugador) {
        jugadorDao.delete(jugador.toEntity())
    }

    override fun getAllJugadores(): Flow<List<Jugador>> {
        return jugadorDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getJugadorById(id: Int): Jugador? {
        return jugadorDao.getById(id)?.toDomain()
    }

    override suspend fun existeNombre(nombre: String): Boolean {
        return jugadorDao.existeNombre(nombre) > 0
    }
}