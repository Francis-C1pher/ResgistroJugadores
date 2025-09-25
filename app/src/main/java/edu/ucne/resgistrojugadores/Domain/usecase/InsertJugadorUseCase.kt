package edu.ucne.RegistroJugadores.Domain.usecase

import edu.ucne.RegistroJugadores.Domain.exceptions.JugadorAlreadyExistsException
import edu.ucne.RegistroJugadores.Domain.exceptions.JugadorDatabaseException
import edu.ucne.RegistroJugadores.Domain.exceptions.JugadorValidationException
import edu.ucne.RegistroJugadores.Domain.exceptions.InvalidPartidasException
import edu.ucne.RegistroJugadores.Domain.model.Jugador
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertJugadorUseCase(
    private val repository: JugadorRepository
) {
    suspend operator fun invoke(jugador: Jugador): Result<Long> {
        return try {
            withContext(Dispatchers.IO) {
                // Validar que el nombre no esté vacío
                if (jugador.nombres.isBlank()) {
                    return@withContext Result.failure(
                        JugadorValidationException("El nombre es obligatorio y no puede estar vacío")
                    )
                }

                // Validar longitud del nombre
                if (jugador.nombres.trim().length < 2) {
                    return@withContext Result.failure(
                        JugadorValidationException("El nombre debe tener al menos 2 caracteres")
                    )
                }

                // Validar que las partidas no sean negativas
                if (jugador.partidas < 0) {
                    return@withContext Result.failure(
                        InvalidPartidasException("Las partidas no pueden ser negativas")
                    )
                }

                try {
                    // ✅ CAMBIO PRINCIPAL: Distinguir entre INSERT y UPDATE
                    val id = if (jugador.jugadorId == null || jugador.jugadorId == 0) {
                        // NUEVO JUGADOR: Validar nombre único
                        if (repository.existeNombre(jugador.nombres.trim())) {
                            return@withContext Result.failure(
                                JugadorAlreadyExistsException("Ya existe un jugador con el nombre '${jugador.nombres.trim()}'")
                            )
                        }

                        // Insertar nuevo jugador
                        repository.insertJugador(jugador.copy(nombres = jugador.nombres.trim()))
                    } else {
                        // JUGADOR EXISTENTE: Actualizar
                        val jugadorActual = repository.getJugadorById(jugador.jugadorId)
                        if (jugadorActual == null) {
                            return@withContext Result.failure(
                                JugadorValidationException("No se encontró el jugador a actualizar")
                            )
                        }

                        // Solo validar nombre único si cambió
                        if (jugadorActual.nombres.trim() != jugador.nombres.trim() &&
                            repository.existeNombre(jugador.nombres.trim())) {
                            return@withContext Result.failure(
                                JugadorAlreadyExistsException("Ya existe un jugador con el nombre '${jugador.nombres.trim()}'")
                            )
                        }

                        // Actualizar jugador existente
                        repository.updateJugador(jugador.copy(nombres = jugador.nombres.trim()))
                        jugador.jugadorId.toLong()
                    }

                    Result.success(id)
                } catch (e: Exception) {
                    Result.failure(JugadorDatabaseException("Error al guardar en la base de datos: ${e.message}"))
                }
            }
        } catch (e: JugadorValidationException) {
            Result.failure(e)
        } catch (e: JugadorAlreadyExistsException) {
            Result.failure(e)
        } catch (e: InvalidPartidasException) {
            Result.failure(e)
        } catch (e: JugadorDatabaseException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(JugadorDatabaseException("Error inesperado: ${e.message}"))
        }
    }
}