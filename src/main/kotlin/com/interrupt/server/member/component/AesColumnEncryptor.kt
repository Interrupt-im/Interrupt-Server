package com.interrupt.server.member.component

import com.interrupt.server.auth.config.AesKeyProperties
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Converter
@Component
class AesColumnEncryptor(
    private val aesKeyProperties: AesKeyProperties
): AttributeConverter<String, String> {

    private val charset = Charsets.UTF_8
    private val encryptCipher: Cipher = initEncryptCipher()
    private val decryptCipher: Cipher = initDecryptCipher()

    private fun initEncryptCipher(): Cipher {
        val secretKey = initSecretKey(aesKeyProperties.secretKey, aesKeyProperties.algorithm)
        val spec = initSpec(aesKeyProperties.iv)

        return Cipher.getInstance(aesKeyProperties.transformation).apply {
            init(Cipher.ENCRYPT_MODE, secretKey, spec)
        }
    }

    private fun initDecryptCipher(): Cipher {
        val secretKey = initSecretKey(aesKeyProperties.secretKey, aesKeyProperties.algorithm)
        val spec = initSpec(aesKeyProperties.iv)

        return Cipher.getInstance(aesKeyProperties.transformation).apply {
            init(Cipher.DECRYPT_MODE, secretKey, spec)
        }
    }

    private fun initSecretKey(key: String, algorithm: String): SecretKey {
        val keyByteArray = Base64.getDecoder().decode(key)
        return SecretKeySpec(keyByteArray, algorithm)
    }

    private fun initSpec(iv: String): IvParameterSpec {
        val ivBytes = Base64.getDecoder().decode(iv)
        return IvParameterSpec(ivBytes)
    }

    override fun convertToDatabaseColumn(attribute: String?): String? =
        attribute?.let {
            val encryptedBytes = encryptCipher.doFinal(it.toByteArray(charset))
            Base64.getEncoder().encodeToString(encryptedBytes)
        } ?: run { null }

    override fun convertToEntityAttribute(dbData: String?): String? =
        dbData?.let {
            val decryptedBytes = decryptCipher.doFinal(Base64.getDecoder().decode(it))
            String(decryptedBytes)
        } ?: run { null }
}