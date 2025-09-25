package edu.ucne.RegistroJugadores

import android.app.Application
import edu.ucne.RegistroJugadores.Data.DataBase.AppDatabase
import edu.ucne.RegistroJugadores.Data.repository.JugadorRepositoryImpl
import edu.ucne.RegistroJugadores.Data.repository.PartidaRepositoryImpl
import edu.ucne.RegistroJugadores.Data.repository.LogroRepositoryImpl
import edu.ucne.RegistroJugadores.Domain.repository.JugadorRepository
import edu.ucne.RegistroJugadores.Domain.repository.PartidaRepository
import edu.ucne.RegistroJugadores.Domain.repository.LogroRepository
import edu.ucne.RegistroJugadores.Domain.usecase.*

class JugadorApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    // Repositories
    val jugadorRepository: JugadorRepository by lazy {
        JugadorRepositoryImpl(database.jugadorDao())
    }

    val partidaRepository: PartidaRepository by lazy {
        PartidaRepositoryImpl(database.partidaDao())
    }

    val logroRepository: LogroRepository by lazy {
        LogroRepositoryImpl(database.logroDao())
    }

    // Jugador Use Cases
    val getJugadoresUseCase by lazy { GetJugadoresUseCase(jugadorRepository) }
    val insertJugadorUseCase by lazy { InsertJugadorUseCase(jugadorRepository) }
    val deleteJugadorUseCase by lazy { DeleteJugadorUseCase(jugadorRepository) }
    val validateJugadorUseCase by lazy { ValidateJugadorUseCase(jugadorRepository) }

    // Partida Use Cases
    val getPartidasUseCase by lazy { GetPartidasUseCase(partidaRepository) }
    val insertPartidaUseCase by lazy { InsertPartidaUseCase(partidaRepository, jugadorRepository) }
    val deletePartidaUseCase by lazy { DeletePartidaUseCase(partidaRepository) }
    val validatePartidaUseCase by lazy { ValidatePartidaUseCase(jugadorRepository) }

    // Logro Use Cases ✅
    val getLogrosUseCase by lazy { GetLogrosUseCase(logroRepository) }
    val insertLogroUseCase by lazy { InsertLogroUseCase(logroRepository) }
    val deleteLogroUseCase by lazy { DeleteLogroUseCase(logroRepository) }
    val validateLogroUseCase by lazy { ValidateLogroUseCase() }
}
