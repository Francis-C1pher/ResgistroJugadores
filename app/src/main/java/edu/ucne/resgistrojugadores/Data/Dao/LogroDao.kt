package edu.ucne.RegistroJugadores.Data.Dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import edu.ucne.RegistroJugadores.Data.Entities.LogroEntity

@Dao
interface LogroDao {
    @Insert
    suspend fun insert(logro: LogroEntity): Long

    @Update
    suspend fun update(logro: LogroEntity)

    @Delete
    suspend fun delete(logro: LogroEntity)

    @Query("SELECT * FROM Logros ORDER BY nombre ASC")
    fun getAll(): Flow<List<LogroEntity>>

    @Query("SELECT * FROM Logros WHERE logroId = :id")
    suspend fun getById(id: Int): LogroEntity?

    @Query("SELECT * FROM Logros WHERE nombre = :nombre")
    suspend fun getByNombre(nombre: String): LogroEntity?

    @Query("SELECT COUNT(*) FROM Logros WHERE nombre = :nombre")
    suspend fun existeNombre(nombre: String): Int

    @Query("SELECT * FROM Logros WHERE nombre LIKE '%' || :busqueda || '%' OR descripcion LIKE '%' || :busqueda || '%'")
    fun buscarLogros(busqueda: String): Flow<List<LogroEntity>>
}