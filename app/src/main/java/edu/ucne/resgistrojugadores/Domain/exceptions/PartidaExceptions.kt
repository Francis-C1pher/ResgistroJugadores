package edu.ucne.RegistroJugadores.Domain.exceptions

/**
 * Excepciones específicas para el dominio de Partida
 */
sealed class PartidaException(message: String) : Exception(message)

class PartidaNotFoundException(message: String) : PartidaException(message)

class PartidaValidationException(message: String) : PartidaException(message)

class PartidaDatabaseException(message: String) : PartidaException(message)

class JugadorInvalidException(message: String) : PartidaException(message)

class PartidaYaFinalizadaException(message: String) : PartidaException(message)