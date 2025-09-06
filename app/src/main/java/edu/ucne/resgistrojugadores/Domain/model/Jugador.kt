package edu.ucne.RegistroJugadores.Domain.model

data class Jugador(
    val jugadorId: Int = 0,
    val nombres: String,
    val partidas: Int
)