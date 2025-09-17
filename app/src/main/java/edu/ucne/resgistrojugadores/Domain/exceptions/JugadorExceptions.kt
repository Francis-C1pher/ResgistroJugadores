package edu.ucne.RegistroJugadores.Domain.exceptions

/**
 * Excepciones específicas para el dominio de Jugador
 */
sealed class JugadorException(message: String) : Exception(message)

class JugadorNotFoundException(message: String) : JugadorException(message)

class JugadorValidationException(message: String) : JugadorException(message)

class JugadorAlreadyExistsException(message: String) : JugadorException(message)

class JugadorDatabaseException(message: String) : JugadorException(message)

class InvalidPartidasException(message: String) : JugadorException(message)