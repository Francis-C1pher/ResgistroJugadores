package edu.ucne.RegistroJugadores.ui.events

import java.util.Date

sealed class RegistroPartidaEvent {
    data class FechaChanged(val fecha: Date) : RegistroPartidaEvent()
    data class Jugador1Changed(val jugador1Id: Int) : RegistroPartidaEvent()
    data class Jugador2Changed(val jugador2Id: Int) : RegistroPartidaEvent()
    data class GanadorChanged(val ganadorId: Int?) : RegistroPartidaEvent()
    data class EsFinalizadaChanged(val esFinalizada: Boolean) : RegistroPartidaEvent()
    object SavePartida : RegistroPartidaEvent()
    object ClearForm : RegistroPartidaEvent()
    data class DeletePartida(val partidaId: Int) : RegistroPartidaEvent()
    data class SelectPartida(val partidaId: Int) : RegistroPartidaEvent()
    object LoadJugadores : RegistroPartidaEvent()
}