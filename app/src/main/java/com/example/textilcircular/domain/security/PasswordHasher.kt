package com.example.textilcircular.domain.security

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Las contraseñas nunca se guardan en texto plano: se almacena "sal:hash" (SHA-256).
 */
object PasswordHasher {

    private val random = SecureRandom()

    fun hash(password: String): String {
        val sal = ByteArray(16).also { random.nextBytes(it) }
        return "${sal.toHex()}:${digest(sal, password).toHex()}"
    }

    fun verificar(password: String, almacenado: String): Boolean {
        val partes = almacenado.split(":")
        if (partes.size != 2) return false
        val sal = partes[0].hexToBytes()
        val esperado = partes[1].hexToBytes()
        return MessageDigest.isEqual(digest(sal, password), esperado)
    }

    private fun digest(sal: ByteArray, password: String): ByteArray =
        MessageDigest.getInstance("SHA-256").run {
            update(sal)
            digest(password.toByteArray(Charsets.UTF_8))
        }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.hexToBytes(): ByteArray =
        chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
