package edu.ucne.RegistroJugadores.ui.events

sealed class RegistroLogroEvent {
    data class NombreChanged(val nombre: String) : RegistroLogroEvent()
    data class DescripcionChanged(val descripcion: String) : RegistroLogroEvent()
    object SaveLogro : RegistroLogroEvent()
    object ClearForm : RegistroLogroEvent()
    data class DeleteLogro(val logroId: Int) : RegistroLogroEvent()
    data class SelectLogro(val logroId: Int) : RegistroLogroEvent()
}