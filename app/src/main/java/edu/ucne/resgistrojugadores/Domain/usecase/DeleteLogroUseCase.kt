package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.exceptions.*
import edu.ucne.RegistroJugadores.Domain.model.Logro
import edu.ucne.RegistroJugadores.Domain.repository.LogroRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteLogroUseCase(
    private val repository: LogroRepository
) {
    suspend operator fun invoke(logro: Logro): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val existingLogro = repository.getLogroById(logro.logroId ?: 0)
                    ?: return@withContext Result.failure(
                        LogroNotFoundException("No se encontró el logro con ID ${logro.logroId}")
                    )

                repository.deleteLogro(existingLogro)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(LogroDatabaseException("Error al eliminar logro: ${e.message}"))
        }
    }

    suspend operator fun invoke(logroId: Int): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val existingLogro = repository.getLogroById(logroId)
                    ?: return@withContext Result.failure(
                        LogroNotFoundException("No se encontró el logro con ID $logroId")
                    )

                repository.deleteLogro(existingLogro)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(LogroDatabaseException("Error al eliminar logro: ${e.message}"))
        }
    }
}