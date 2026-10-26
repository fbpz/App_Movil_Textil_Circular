package com.example.textilcircular

import com.example.textilcircular.domain.security.PasswordHasher
import com.example.textilcircular.domain.validation.CampoFormulario
import com.example.textilcircular.domain.validation.Validadores
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadoresTest {

    @Test
    fun email_valido_e_invalido() {
        assertNull(Validadores.email("maria@textilcircular.cl"))
        assertNotNull(Validadores.email(""))
        assertNotNull(Validadores.email("maria@"))
        assertNotNull(Validadores.email("maria.cl"))
    }

    @Test
    fun nombre_requiere_letras_y_largo_minimo() {
        assertNull(Validadores.nombrePersona("María José Ñúñez"))
        assertNotNull(Validadores.nombrePersona("Al"))
        assertNotNull(Validadores.nombrePersona("Juan123"))
    }

    @Test
    fun password_exige_largo_mayuscula_y_numero() {
        assertNull(Validadores.password("Textil2025"))
        assertNotNull(Validadores.password("corta1A"))
        assertNotNull(Validadores.password("sinmayuscula1"))
        assertNotNull(Validadores.password("SinNumeroAqui"))
    }

    @Test
    fun confirmacion_de_password() {
        assertNull(Validadores.confirmarPassword("Textil2025", "Textil2025"))
        assertNotNull(Validadores.confirmarPassword("Textil2025", "Textil2024"))
    }

    @Test
    fun rut_con_digito_verificador() {
        assertNull(Validadores.rut("12.345.678-5"))
        assertNull(Validadores.rut("11111111-1"))
        assertNotNull(Validadores.rut("12.345.678-9"))
        assertNotNull(Validadores.rut("abc"))
        assertEquals('K', Validadores.calcularDv("10000013"))
    }

    @Test
    fun telefono_movil_chileno() {
        assertNull(Validadores.telefonoChile("9 1234 5678"))
        assertNull(Validadores.telefonoChile("+56 9 1234 5678"))
        assertNotNull(Validadores.telefonoChile("2 2123 4567"))
        assertNotNull(Validadores.telefonoChile("12345"))
    }

    @Test
    fun cantidad_en_rango() {
        assertNull(Validadores.cantidad("25"))
        assertNotNull(Validadores.cantidad("0"))
        assertNotNull(Validadores.cantidad("501"))
        assertNotNull(Validadores.cantidad("diez"))
    }

    @Test
    fun instagram_es_opcional_pero_validado() {
        assertNull(Validadores.instagramOpcional(""))
        assertNull(Validadores.instagramOpcional("@mi.marca_circular"))
        assertNotNull(Validadores.instagramOpcional("@marca con espacios"))
    }

    @Test
    fun campo_muestra_error_solo_tras_interaccion() {
        val inicial = CampoFormulario()
        assertNull(inicial.errorVisible)
        val validado = inicial.validar(Validadores::email)
        assertNotNull(validado.errorVisible)
        val corregido = validado.actualizar("ana@correo.cl", Validadores::email)
        assertTrue(corregido.esValido)
    }

    @Test
    fun password_se_guarda_hasheada_y_se_verifica() {
        val hash = PasswordHasher.hash("Textil2025")
        assertFalse(hash.contains("Textil2025"))
        assertTrue(PasswordHasher.verificar("Textil2025", hash))
        assertFalse(PasswordHasher.verificar("otraClave1", hash))
    }
}
