package edu.ucne.RegistroJugadores

import android.app.Application
import edu.ucne.RegistroJugadores.Data.DataBase.AppDatabase
import edu.ucne.RegistroJugadores.Data.repository.JugadorRepositoryImpl
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository
import edu.ucne.RegistroJugadores.Domain.usecase.DeleteJugadorUseCase
import edu.ucne.RegistroJugadores.Domain.usecase.GetJugadoresUseCase
import edu.ucne.RegistroJugadores.Domain.usecase.InsertJugadorUseCase
import edu.ucne.RegistroJugadores.Domain.usecase.ValidateJugadorUseCase

class JugadorApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    val repository: JugadorRepository by lazy {
        JugadorRepositoryImpl(database.jugadorDao())
    }

    val getJugadoresUseCase by lazy { GetJugadoresUseCase(repository) }
    val insertJugadorUseCase by lazy { InsertJugadorUseCase(repository) }
    val deleteJugadorUseCase by lazy { DeleteJugadorUseCase(repository) }
    val validateJugadorUseCase by lazy { ValidateJugadorUseCase(repository) }
}