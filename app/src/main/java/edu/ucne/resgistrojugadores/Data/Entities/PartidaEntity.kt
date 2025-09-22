package edu.ucne.RegistroJugadores.Data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "Partidas",
    foreignKeys = [
        ForeignKey(
            entity = JugadorEntity::class,
            parentColumns = ["jugadorId"],
            childColumns = ["jugador1Id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = JugadorEntity::class,
            parentColumns = ["jugadorId"],
            childColumns = ["jugador2Id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = JugadorEntity::class,
            parentColumns = ["jugadorId"],
            childColumns = ["ganadorId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["jugador1Id"]),
        Index(value = ["jugador2Id"]),
        Index(value = ["ganadorId"])
    ]
)
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true)
    val partidaId: Int? = null,
    val fecha: Date,
    val jugador1Id: Int,
    val jugador2Id: Int,
    val ganadorId: Int? = null,
    val esFinalizada: Boolean = false
)