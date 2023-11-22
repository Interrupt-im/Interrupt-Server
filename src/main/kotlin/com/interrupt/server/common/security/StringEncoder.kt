package com.interrupt.server.common.security

import org.springframework.beans.factory.annotation.Value
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class StringEncoder {

    @Value("\${security.encrypt-key}")
    private lateinit var secretKey: String

    fun encrypt(originalString: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)

        val encryptedBytes = cipher.doFinal(originalString.toByteArray(Charsets.UTF_8))

        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encryptedString: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)

        val encryptedBytes = Base64.getDecoder().decode(encryptedString)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, Charsets.UTF_8)
    }

}
