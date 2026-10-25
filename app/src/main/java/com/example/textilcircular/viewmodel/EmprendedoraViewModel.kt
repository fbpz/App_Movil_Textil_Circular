package com.example.textilcircular.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textilcircular.data.Emprendedora
import com.example.textilcircular.data.EmprendedoraDao
import kotlinx.coroutines.launch

class EmprendedoraViewModel(private val dao: EmprendedoraDao) : ViewModel() {

    // Variables de estado para el formulario
    var nombre = mutableStateOf("")
    var nombreComercial = mutableStateOf("")
    var rubro = mutableStateOf("")

    // Estado para manejar los errores visuales
    var errorMensaje = mutableStateOf("")

    // Función para validar y guardar en la base de datos local
    fun guardarEmprendedora(onSuccess: () -> Unit) {
        // Validación: Verifica que ningún campo esté vacío
        if (nombre.value.isBlank() || nombreComercial.value.isBlank() || rubro.value.isBlank()) {
            errorMensaje.value = "Por favor, completa todos los campos"
            return
        }

        errorMensaje.value = "" // Limpiar errores si todo está bien

        // Guardar en la base de datos (Persistencia local)
        viewModelScope.launch {
            val nuevaEmprendedora = Emprendedora(
                nombre = nombre.value,
                nombreComercial = nombreComercial.value,
                rubro = rubro.value
            )
            dao.insertar(nuevaEmprendedora)

            // Limpiar el formulario después de guardar
            nombre.value = ""
            nombreComercial.value = ""
            rubro.value = ""

            onSuccess() // Avisar a la vista que se guardó con éxito
        }
    }
}