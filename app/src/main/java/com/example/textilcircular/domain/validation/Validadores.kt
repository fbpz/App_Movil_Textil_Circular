package com.example.textilcircular.domain.validation

/**
 * Lógica de validación centralizada y desacoplada de la UI.
 * Cada función devuelve null si el valor es válido o el mensaje de error a mostrar.
 * No depende de Android, por lo que se prueba con tests unitarios (ver ValidadoresTest).
 */
object Validadores {

    private val REGEX_EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val REGEX_SOLO_LETRAS = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' ]+$")
    private val REGEX_INSTAGRAM = Regex("^@?[A-Za-z0-9._]{1,30}$")

    /** Campo obligatorio sin formato específico; [mensaje] es el texto completo a mostrar. */
    fun requerido(valor: String, mensaje: String): String? =
        if (valor.isBlank()) mensaje else null

    fun nombrePersona(valor: String): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "El nombre es obligatorio"
            v.length < 3 -> "Debe tener al menos 3 caracteres"
            v.length > 50 -> "Máximo 50 caracteres"
            !REGEX_SOLO_LETRAS.matches(v) -> "Solo se permiten letras y espacios"
            else -> null
        }
    }

    fun nombreComercial(valor: String): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "El nombre comercial es obligatorio"
            v.length < 2 -> "Debe tener al menos 2 caracteres"
            v.length > 40 -> "Máximo 40 caracteres"
            else -> null
        }
    }

    fun email(valor: String): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "El correo es obligatorio"
            !REGEX_EMAIL.matches(v) -> "Formato de correo inválido (ej: nombre@correo.cl)"
            else -> null
        }
    }

    fun password(valor: String): String? = when {
        valor.isEmpty() -> "La contraseña es obligatoria"
        valor.length < 8 -> "Debe tener al menos 8 caracteres"
        !valor.any { it.isUpperCase() } -> "Debe incluir al menos una mayúscula"
        !valor.any { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    fun confirmarPassword(password: String, confirmacion: String): String? = when {
        confirmacion.isEmpty() -> "Confirma tu contraseña"
        password != confirmacion -> "Las contraseñas no coinciden"
        else -> null
    }

    /** Teléfono móvil chileno: 9 dígitos que comienzan con 9, con o sin prefijo +56. */
    fun telefonoChile(valor: String): String? {
        if (valor.isBlank()) return "El teléfono es obligatorio"
        val digitos = valor.filter { it.isDigit() }
        val local = if (digitos.startsWith("56") && digitos.length == 11) digitos.drop(2) else digitos
        return when {
            local.length != 9 -> "Debe tener 9 dígitos (ej: 9 1234 5678)"
            !local.startsWith("9") -> "El número móvil debe comenzar con 9"
            else -> null
        }
    }

    /** RUT chileno con dígito verificador (módulo 11). Acepta 12.345.678-5 o 12345678-5. */
    fun rut(valor: String): String? {
        if (valor.isBlank()) return "El RUT es obligatorio"
        val limpio = valor.replace(".", "").replace("-", "").uppercase()
        if (limpio.length < 8 || limpio.length > 9) return "RUT inválido (ej: 12.345.678-5)"
        val cuerpo = limpio.dropLast(1)
        val dv = limpio.last()
        if (!cuerpo.all { it.isDigit() }) return "RUT inválido (ej: 12.345.678-5)"
        return if (calcularDv(cuerpo) == dv) null else "El dígito verificador no es correcto"
    }

    fun calcularDv(cuerpo: String): Char {
        var suma = 0
        var multiplicador = 2
        for (c in cuerpo.reversed()) {
            suma += (c - '0') * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }
        return when (val resto = 11 - (suma % 11)) {
            11 -> '0'
            10 -> 'K'
            else -> ('0' + resto)
        }
    }

    fun seleccion(valor: String?, campo: String): String? =
        if (valor.isNullOrBlank()) "Selecciona $campo" else null

    fun comuna(valor: String): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "La comuna es obligatoria"
            v.length < 3 -> "Ingresa un nombre de comuna válido"
            !REGEX_SOLO_LETRAS.matches(v) -> "Solo se permiten letras y espacios"
            else -> null
        }
    }

    fun descripcion(valor: String, minimo: Int = 20, maximo: Int = 300): String? {
        val v = valor.trim()
        return when {
            v.isEmpty() -> "La descripción es obligatoria"
            v.length < minimo -> "Describe con al menos $minimo caracteres (${v.length}/$minimo)"
            v.length > maximo -> "Máximo $maximo caracteres"
            else -> null
        }
    }

    /** Campo opcional: solo se valida si el usuario escribió algo. */
    fun instagramOpcional(valor: String): String? {
        val v = valor.trim()
        if (v.isEmpty()) return null
        return if (REGEX_INSTAGRAM.matches(v)) null else "Usuario de Instagram inválido (ej: @mimarca)"
    }

    fun cantidad(valor: String, minimo: Int = 1, maximo: Int = 500): String? {
        if (valor.isBlank()) return "La cantidad es obligatoria"
        val n = valor.toIntOrNull() ?: return "Ingresa solo números"
        return when {
            n < minimo -> "La cantidad mínima es $minimo"
            n > maximo -> "Para más de $maximo prendas contáctanos directamente"
            else -> null
        }
    }

    fun comentarioOpcional(valor: String, maximo: Int = 200): String? =
        if (valor.length > maximo) "Máximo $maximo caracteres" else null

    fun aceptaTerminos(acepta: Boolean): String? =
        if (!acepta) "Debes aceptar los términos para continuar" else null
}
