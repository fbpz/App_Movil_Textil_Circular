package com.example.textilcircular.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.textilcircular.data.local.entity.Donacion
import com.example.textilcircular.data.local.entity.Emprendedora
import com.example.textilcircular.data.local.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun observarPorId(id: Long): Flow<Usuario?>
}

@Dao
interface EmprendedoraDao {
    @Insert
    suspend fun insertar(emprendedora: Emprendedora): Long

    @Update
    suspend fun actualizar(emprendedora: Emprendedora)

    @Delete
    suspend fun eliminar(emprendedora: Emprendedora)

    @Query("SELECT * FROM emprendedoras ORDER BY fechaRegistro DESC")
    fun observarTodas(): Flow<List<Emprendedora>>

    @Query("SELECT * FROM emprendedoras WHERE id = :id")
    fun observarPorId(id: Long): Flow<Emprendedora?>

    @Query("SELECT * FROM emprendedoras WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Emprendedora?

    @Query("UPDATE emprendedoras SET favorita = :favorita WHERE id = :id")
    suspend fun marcarFavorita(id: Long, favorita: Boolean)

    @Query("SELECT COUNT(*) FROM emprendedoras")
    fun contar(): Flow<Int>
}

@Dao
interface DonacionDao {
    @Insert
    suspend fun insertar(donacion: Donacion): Long

    @Query("SELECT * FROM donaciones WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<Donacion>>

    @Query("SELECT COALESCE(SUM(cantidad), 0) FROM donaciones")
    fun totalPrendas(): Flow<Int>
}
