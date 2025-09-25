package edu.ucne.RegistroJugadores.Domain.usecase

class ValidateLogroUseCase {

    fun validateNombre(nombre: String): String? {
        return if (nombre.isBlank()) {
            "El nombre no puede estar vacío"
        } else {
            null
        }
    }

    fun validateDescripcion(descripcion: String): String? {
        return if (descripcion.isBlank()) {
            "La descripción no puede estar vacía"
        } else {
            null
        }
    }
}
