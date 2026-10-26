package com.example.textilcircular.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.textilcircular.data.local.dao.DonacionDao
import com.example.textilcircular.data.local.dao.EmprendedoraDao
import com.example.textilcircular.data.local.dao.UsuarioDao
import com.example.textilcircular.data.local.entity.Donacion
import com.example.textilcircular.data.local.entity.Emprendedora
import com.example.textilcircular.data.local.entity.Usuario

@Database(
    entities = [Usuario::class, Emprendedora::class, Donacion::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun emprendedoraDao(): EmprendedoraDao
    abstract fun donacionDao(): DonacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "textil_database"
                )
                    // La versión 1 (prototipo) no tenía datos reales: se recrea el esquema
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
