package edu.ucne.RegistroJugadores.Data.mappers

import edu.ucne.RegistroJugadores.Data.Entities.JugadorEntity
import edu.ucne.RegistroJugadores.Domain.model.Jugador


fun JugadorEntity.toDomain(): Jugador {
    return Jugador(
        jugadorId = jugadorId,
        nombres = nombres,
        partidas = partidas
    )
}

fun Jugador.toEntity(): JugadorEntity {
    return JugadorEntity(
        jugadorId = jugadorId,
        nombres = nombres,
        partidas = partidas
    )
}