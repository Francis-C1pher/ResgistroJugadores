package edu.ucne.RegistroJugadores.Data.mappers

import edu.ucne.RegistroJugadores.Data.Entities.PartidaEntity
import edu.ucne.RegistroJugadores.Domain.model.Partida

fun PartidaEntity.toDomain(): Partida {
    return Partida(
        partidaId = partidaId,
        fecha = fecha,
        jugador1Id = jugador1Id,
        jugador2Id = jugador2Id,
        ganadorId = ganadorId,
        esFinalizada = esFinalizada
    )
}

fun Partida.toEntity(): PartidaEntity {
    return PartidaEntity(
        partidaId = partidaId,
        fecha = fecha,
        jugador1Id = jugador1Id,
        jugador2Id = jugador2Id,
        ganadorId = ganadorId,
        esFinalizada = esFinalizada
    )
}