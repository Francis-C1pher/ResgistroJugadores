package edu.ucne.RegistroJugadores.ui.state


import edu.ucne.RegistroJugadores.Domain.model.Jugador

data class RegistroJugadorState(
    val jugadorId: Int = 0,
    val nombres: String = "",
    val partidas: String = "",
    val nombresError: String? = null,
    val partidasError: String? = null,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val jugadores: List<Jugador> = emptyList()
)