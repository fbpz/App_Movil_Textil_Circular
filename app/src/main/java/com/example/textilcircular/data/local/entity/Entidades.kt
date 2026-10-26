package com.example.textilcircular.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios", indices = [Index(value = ["email"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val rut: String,
    val email: String,
    val passwordHash: String,
    val fechaRegistro: Long = System.currentTimeMillis()
)

@Entity(tableName = "emprendedoras")
data class Emprendedora(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val nombreComercial: String,
    val categoria: String,          // nombre del enum Categoria
    val region: String,
    val comuna: String,
    val telefono: String,
    val email: String,
    val instagram: String? = null,
    val descripcion: String,
    val fotoPath: String? = null,   // ruta local de la foto (cámara o galería)
    val latitud: Double? = null,    // ubicación GPS del taller / punto de venta
    val longitud: Double? = null,
    val favorita: Boolean = false,
    val creadaPor: Long,
    val fechaRegistro: Long = System.currentTimeMillis()
)

@Entity(tableName = "donaciones")
data class Donacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val nombreContacto: String,
    val telefono: String,
    val region: String,
    val comuna: String,
    val tipoPrenda: String,
    val cantidad: Int,
    val estado: String,             // nombre del enum EstadoPrenda
    val comentario: String? = null,
    val fecha: Long = System.currentTimeMillis()
)
