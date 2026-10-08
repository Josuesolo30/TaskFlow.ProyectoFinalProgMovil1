package edu.unicda.lab3

/** Errores del "backend" simulado. Los estudiantes NO deben cambiar estas clases. */
sealed class LegacyException(message: String) : Exception(message)

class AuthException(message: String) : LegacyException(message)
class NotFoundException(message: String) : LegacyException(message)
class NetworkException(message: String) : LegacyException(message)

