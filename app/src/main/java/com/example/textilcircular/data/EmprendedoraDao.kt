package com.example.textilcircular.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmprendedoraDao {
    @Insert
    suspend fun insertar(emprendedora: Emprendedora)

    @Query("SELECT * FROM emprendedoras")
    fun obtenerTodas(): Flow<List<Emprendedora>>
}