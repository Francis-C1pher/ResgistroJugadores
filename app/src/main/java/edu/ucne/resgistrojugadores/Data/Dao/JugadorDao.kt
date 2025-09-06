package edu.ucne.RegistroJugadores.Data.Dao


import androidx.room.*
import kotlinx.coroutines.flow.Flow
import edu.ucne.RegistroJugadores.Data.Entities.JugadorEntity
@Dao
interface JugadorDao {
    @Insert
    suspend fun insert(jugador: JugadorEntity): Long

    @Update
    suspend fun update(jugador: JugadorEntity)

    @Delete
    suspend fun delete(jugador: JugadorEntity)

    @Query("SELECT * FROM Jugadores")
    fun getAll(): Flow<List<JugadorEntity>>

    @Query("SELECT * FROM Jugadores WHERE jugadorId = :id")
    suspend fun getById(id: Int): JugadorEntity?

    @Query("SELECT * FROM Jugadores WHERE nombres = :nombre")
    suspend fun getByNombre(nombre: String): JugadorEntity?

    @Query("SELECT COUNT(*) FROM Jugadores WHERE nombres = :nombre")
    suspend fun existeNombre(nombre: String): Int
}