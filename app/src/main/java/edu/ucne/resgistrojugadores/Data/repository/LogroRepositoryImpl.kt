package edu.ucne.RegistroJugadores.Data.repository

import edu.ucne.RegistroJugadores.Data.Dao.LogroDao
import edu.ucne.RegistroJugadores.Data.Entities.LogroEntity
import edu.ucne.RegistroJugadores.Domain.model.Logro
import edu.ucne.RegistroJugadores.Domain.repository.LogroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LogroRepositoryImpl(
    private val logroDao: LogroDao
) : LogroRepository {

    override suspend fun insertLogro(logro: Logro): Long {
        return logroDao.insert(logro.toEntity())
    }

    override suspend fun updateLogro(logro: Logro) {
        logroDao.update(logro.toEntity())
    }

    override suspend fun deleteLogro(logro: Logro) {
        logroDao.delete(logro.toEntity())
    }

    override fun getAllLogros(): Flow<List<Logro>> {
        return logroDao.getAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getLogroById(id: Int): Logro? {
        return logroDao.getById(id)?.toDomain()
    }

    override suspend fun existeNombre(nombre: String): Boolean {
        // ✅ Convertimos el Int del DAO a Boolean
        return logroDao.existeNombre(nombre) > 0
    }
}

// Mappers
fun LogroEntity.toDomain(): Logro {
    return Logro(
        logroId = logroId,
        nombre = nombre,
        descripcion = descripcion
    )
}

fun Logro.toEntity(): LogroEntity {
    return LogroEntity(
        logroId = logroId,
        nombre = nombre,
        descripcion = descripcion
    )
}
