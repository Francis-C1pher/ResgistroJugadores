package edu.ucne.RegistroJugadores.Domain.exceptions

/**
 * Excepciones específicas para el dominio de Logro
 */
sealed class LogroException(message: String) : Exception(message)

// Cuando no se encuentra un logro en la BD
class LogroNotFoundException(message: String) : LogroException(message)

// Cuando los datos de un logro no cumplen las reglas de validación
class LogroValidationException(message: String) : LogroException(message)

// Cuando ya existe un logro con el mismo nombre
class LogroAlreadyExistsException(message: String) : LogroException(message)

// Errores relacionados con operaciones de base de datos
class LogroDatabaseException(message: String) : LogroException(message)
