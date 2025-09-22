package edu.ucne.RegistroJugadores.Data.Dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import edu.ucne.RegistroJugadores.Data.Entities.PartidaEntity

@Dao
interface PartidaDao {
    @Insert
    suspend fun insert(partida: PartidaEntity): Long

    @Update
    suspend fun update(partida: PartidaEntity)

    @Delete
    suspend fun delete(partida: PartidaEntity)

    @Query("SELECT * FROM Partidas ORDER BY fecha DESC")
    fun getAll(): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM Partidas WHERE partidaId = :id")
    suspend fun getById(id: Int): PartidaEntity?

    @Query("""
        SELECT * FROM Partidas 
        WHERE jugador1Id = :jugadorId OR jugador2Id = :jugadorId 
        ORDER BY fecha DESC
    """)
    fun getByJugador(jugadorId: Int): Flow<List<PartidaEntity>>

    @Query("""
        SELECT COUNT(*) FROM Partidas 
        WHERE jugador1Id = :jugadorId OR jugador2Id = :jugadorId
    """)
    suspend fun getPartidasCountByJugador(jugadorId: Int): Int

    @Query("SELECT * FROM Partidas WHERE esFinalizada = :esFinalizada ORDER BY fecha DESC")
    fun getByEstado(esFinalizada: Boolean): Flow<List<PartidaEntity>>

    @Query("""
        SELECT COUNT(*) FROM Partidas 
        WHERE ganadorId = :jugadorId AND esFinalizada = 1
    """)
    suspend fun getVictoriasByJugador(jugadorId: Int): Int

    @Query("SELECT * FROM Partidas WHERE ganadorId IS NULL AND esFinalizada = 0")
    fun getPartidasSinGanador(): Flow<List<PartidaEntity>>
}