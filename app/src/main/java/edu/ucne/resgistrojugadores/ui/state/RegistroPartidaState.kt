package edu.ucne.RegistroJugadores.ui.state

import edu.ucne.RegistroJugadores.Domain.model.Jugador
import edu.ucne.RegistroJugadores.Domain.model.Partida
import java.util.Date

data class RegistroPartidaState(
    val partidaId: Int = 0,
    val fecha: Date = Date(),
    val jugador1Id: Int = 0,
    val jugador2Id: Int = 0,
    val ganadorId: Int? = null,
    val esFinalizada: Boolean = false,

    // Errores de validación
    val jugador1Error: String? = null,
    val jugador2Error: String? = null,
    val ganadorError: String? = null,
    val fechaError: String? = null,

    // Estados generales
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,

    // Listas
    val partidas: List<Partida> = emptyList(),
    val jugadoresDisponibles: List<Jugador> = emptyList(),

    // Helpers para mostrar nombres en lugar de IDs
    val jugador1Nombre: String = "",
    val jugador2Nombre: String = "",
    val ganadorNombre: String = ""
)