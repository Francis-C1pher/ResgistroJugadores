package edu.ucne.RegistroJugadores.Data.DataBase

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import edu.ucne.RegistroJugadores.Data.Dao.JugadorDao
import edu.ucne.RegistroJugadores.Data.Dao.LogroDao
import edu.ucne.RegistroJugadores.Data.Dao.PartidaDao
import edu.ucne.RegistroJugadores.Data.Entities.JugadorEntity
import edu.ucne.RegistroJugadores.Data.Entities.LogroEntity
import edu.ucne.RegistroJugadores.Data.Entities.PartidaEntity

// Convertidores de fecha
import androidx.room.TypeConverter
import java.util.Date

class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(
    entities = [
        JugadorEntity::class,
        PartidaEntity::class,
        LogroEntity::class  // ✅ NUEVA ENTIDAD
    ],
    version = 3,  // ✅ INCREMENTAR VERSIÓN
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jugadorDao(): JugadorDao
    abstract fun partidaDao(): PartidaDao
    abstract fun logroDao(): LogroDao  // ✅ NUEVO DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jugador_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}