package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.model.Logro
import edu.ucne.RegistroJugadores.Domain.repository.LogroRepository
import kotlinx.coroutines.flow.Flow

class GetLogrosUseCase(
    private val repository: LogroRepository
) {
    operator fun invoke(): Flow<List<Logro>> {
        return repository.getAllLogros()
    }
}
