package com.example.textilcircular.domain.validation

/**
 * Estado de un campo de formulario: valor actual y error de validación.
 * El error solo se muestra una vez que el usuario interactuó con el campo
 * o intentó enviar el formulario.
 */
data class CampoFormulario(
    val valor: String = "",
    val error: String? = null,
    val tocado: Boolean = false
) {
    val errorVisible: String? get() = if (tocado) error else null
    val esValido: Boolean get() = error == null

    fun actualizar(nuevoValor: String, validador: (String) -> String?): CampoFormulario =
        copy(valor = nuevoValor, error = validador(nuevoValor), tocado = true)

    fun validar(validador: (String) -> String?): CampoFormulario =
        copy(error = validador(valor), tocado = true)
}
