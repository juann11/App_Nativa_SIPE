package com.sipe.app.data

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hash de contraseñas con PBKDF2-HMAC-SHA256 y salt por usuario.
 * Nunca se guarda la contraseña en texto plano.
 */
object Security {

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hash(password: String, saltBase64: String): String {
        val salt = Base64.getDecoder().decode(saltBase64)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        spec.clearPassword()
        return Base64.getEncoder().encodeToString(hash)
    }

    fun verify(password: String, saltBase64: String, expectedHash: String): Boolean {
        return hash(password, saltBase64) == expectedHash
    }

    /** Usuarios creados antes del PBKDF2 (hashCode simple): solo para migrarlos al entrar. */
    fun verifyLegacy(password: String, expected: String): Boolean {
        return password.hashCode().toString() == expected
    }
}
