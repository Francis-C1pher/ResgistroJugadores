package edu.ucne.RegistroJugadores.Data.mappers

import edu.ucne.RegistroJugadores.Data.Entities.LogroEntity
import edu.ucne.RegistroJugadores.Domain.model.Logro

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