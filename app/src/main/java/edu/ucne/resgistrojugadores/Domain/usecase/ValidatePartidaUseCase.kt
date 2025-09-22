package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.exceptions.*
import edu.ucne.RegistroJugadores.Domain.model.Partida
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository
import edu.ucne.RegistroJugadores.Domain.repository.PartidaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

class ValidatePartidaUseCase(
    private val jugadorRepository: JugadorRepository
) {
    suspend fun validateJugador1(jugador1Id: Int): String? {
        return if (jugador1Id <= 0) {
            "Debe seleccionar el Jugador 1"
        } else {
            val jugador = jugadorRepository.getJugadorById(jugador1Id)
            if (jugador == null) "El Jugador 1 seleccionado no existe" else null
        }
    }

    suspend fun validateJugador2(jugador2Id: Int): String? {
        return if (jugador2Id <= 0) {
            "Debe seleccionar el Jugador 2"
        } else {
            val jugador = jugadorRepository.getJugadorById(jugador2Id)
            if (jugador == null) "El Jugador 2 seleccionado no existe" else null
        }
    }

    fun validateJugadoresDiferentes(jugador1Id: Int, jugador2Id: Int): String? {
        return if (jugador1Id == jugador2Id && jugador1Id > 0) {
            "Los jugadores deben ser diferentes"
        } else null
    }

    fun validateGanador(ganadorId: Int?, jugador1Id: Int, jugador2Id: Int): String? {
        return if (ganadorId != null && ganadorId > 0) {
            if (ganadorId != jugador1Id && ganadorId != jugador2Id) {
                "El ganador debe ser uno de los jugadores de la partida"
            } else null
        } else null
    }

    fun validateFecha(fecha: Date): String? {
        return if (fecha.after(Date())) {
            "La fecha no puede ser futura"
        } else null
    }
}