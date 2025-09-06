package edu.ucne.RegistroJugadores.Data.DataBase

import edu.ucne.RegistroJugadores.Data.Dao.JugadorDao
import edu.ucne.RegistroJugadores.Data.Entities.JugadorEntity
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context



@Database(
    entities = [JugadorEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jugadorDao(): JugadorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jugadores_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}