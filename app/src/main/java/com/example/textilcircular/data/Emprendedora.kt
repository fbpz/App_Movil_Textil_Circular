package com.example.textilcircular.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emprendedoras")
data class Emprendedora(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val nombreComercial: String,
    val rubro: String,
    val fotoUri: String? = null // Aquí guardaremos la ruta de la foto de la cámara
)