package edu.ucne.RegistroJugadores.Data.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Logros")
data class LogroEntity(
    @PrimaryKey(autoGenerate = true)
    val logroId: Int? = null,
    val nombre: String,
    val descripcion: String
)