package edu.ucne.RegistroJugadores.Domain.repository


import edu.ucne.RegistroJugadores.Domain.model.Jugador
import kotlinx.coroutines.flow.Flow

interface JugadorRepository {
    suspend fun insertJugador(jugador: Jugador): Long
    suspend fun updateJugador(jugador: Jugador)
    suspend fun deleteJugador(jugador: Jugador)
    fun getAllJugadores(): Flow<List<Jugador>>
    suspend fun getJugadorById(id: Int): Jugador?
    suspend fun existeNombre(nombre: String): Boolean
}