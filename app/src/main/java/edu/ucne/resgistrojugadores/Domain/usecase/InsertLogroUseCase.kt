package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.exceptions.*
import edu.ucne.RegistroJugadores.Domain.model.Logro
import edu.ucne.RegistroJugadores.Domain.repository.LogroRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertLogroUseCase(
    private val repository: LogroRepository
) {
    suspend operator fun invoke(logro: Logro): Result<Long> {
        return try {
            withContext(Dispatchers.IO) {
                // Validar que el nombre no esté vacío
                if (logro.nombre.isBlank()) {
                    return@withContext Result.failure(
                        LogroValidationException("El nombre es obligatorio y no puede estar vacío")
                    )
                }

                // Validar que la descripción no esté vacía
                if (logro.descripcion.isBlank()) {
                    return@withContext Result.failure(
                        LogroValidationException("La descripción es obligatoria y no puede estar vacía")
                    )
                }

                // Validar longitud del nombre
                if (logro.nombre.trim().length < 2) {
                    return@withContext Result.failure(
                        LogroValidationException("El nombre debe tener al menos 2 caracteres")
                    )
                }

                // Validar longitud de la descripción
                if (logro.descripcion.trim().length < 5) {
                    return@withContext Result.failure(
                        LogroValidationException("La descripción debe tener al menos 5 caracteres")
                    )
                }

                try {
                    val id = if (logro.logroId == null || logro.logroId == 0) {
                        // NUEVO LOGRO: Validar nombre único
                        if (repository.existeNombre(logro.nombre.trim())) {
                            return@withContext Result.failure(
                                LogroAlreadyExistsException("Ya existe un logro con el nombre '${logro.nombre.trim()}'")
                            )
                        }

                        // Insertar nuevo logro
                        repository.insertLogro(
                            logro.copy(
                                nombre = logro.nombre.trim(),
                                descripcion = logro.descripcion.trim()
                            )
                        )
                    } else {
                        // LOGRO EXISTENTE: Actualizar
                        val logroActual = repository.getLogroById(logro.logroId)
                        if (logroActual == null) {
                            return@withContext Result.failure(
                                LogroValidationException("No se encontró el logro a actualizar")
                            )
                        }

                        // Solo validar nombre único si cambió
                        if (logroActual.nombre.trim() != logro.nombre.trim() &&
                            repository.existeNombre(logro.nombre.trim())) {
                            return@withContext Result.failure(
                                LogroAlreadyExistsException("Ya existe un logro con el nombre '${logro.nombre.trim()}'")
                            )
                        }

                        // Actualizar logro existente
                        repository.updateLogro(
                            logro.copy(
                                nombre = logro.nombre.trim(),
                                descripcion = logro.descripcion.trim()
                            )
                        )
                        logro.logroId.toLong()
                    }

                    Result.success(id)
                } catch (e: Exception) {
                    Result.failure(LogroDatabaseException("Error al guardar en la base de datos: ${e.message}"))
                }
            }
        } catch (e: LogroValidationException) {
            Result.failure(e)
        } catch (e: LogroAlreadyExistsException) {
            Result.failure(e)
        } catch (e: LogroDatabaseException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(LogroDatabaseException("Error inesperado: ${e.message}"))
        }
    }
}