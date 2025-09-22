package edu.ucne.RegistroJugadores.Domain.model

import java.util.Date

data class Partida(
    val partidaId: Int? = null,
    val fecha: Date,
    val jugador1Id: Int,
    val jugador2Id: Int,
    val ganadorId: Int? = null, // null si no hay ganador aún, o empate
    val esFinalizada: Boolean = false
)