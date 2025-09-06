package edu.ucne.RegistroJugadores.ui.events

sealed class RegistroJugadorEvent {
    data class NombresChanged(val nombres: String) : RegistroJugadorEvent()
    data class PartidasChanged(val partidas: String) : RegistroJugadorEvent()
    object SaveJugador : RegistroJugadorEvent()
    object ClearForm : RegistroJugadorEvent()
    data class DeleteJugador(val jugadorId: Int) : RegistroJugadorEvent()
    data class SelectJugador(val jugadorId: Int) : RegistroJugadorEvent()

}