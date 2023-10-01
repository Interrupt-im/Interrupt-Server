package com.interrupt.server.common.security

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import org.springframework.beans.factory.annotation.Value
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class StringEncoder {

    @Value("\${security.encrypt-key}")
    private lateinit var secretKey: String

    @Value("\${security.pre-salt}")
    private lateinit var preSalt: String

    @Value("\${security.post-salt}")
    private lateinit var postSalt: String

    fun encrypt(originalString: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)

        val encryptedBytes = cipher.doFinal(addSalt(originalString, preSalt, postSalt).toByteArray(Charsets.UTF_8))

        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encryptedString: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)

        val encryptedBytes = Base64.getDecoder().decode(encryptedString)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return removeSalt(String(decryptedBytes, Charsets.UTF_8), preSalt, postSalt)
    }

    private fun addSalt(originalString: String, preSalt: String, postSalt: String): String = preSalt + originalString + postSalt

    private fun removeSalt(saltedString: String, preSalt: String, postSalt: String): String {
        if (saltedString.startsWith(preSalt).not() || saltedString.endsWith(postSalt).not()) throw InterruptServerException(errorCode = ErrorCode.NOT_SALTED_STRING)

        val startIndex = preSalt.length
        val endIndex = saltedString.length - postSalt.length

        return saltedString.substring(startIndex, endIndex)
    }

}
