package com.interrupt.server.auth.service

import com.interrupt.server.auth.config.RsaKeyProperties
import jakarta.annotation.PostConstruct
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

@Converter
@Component
class RsaColumnEncryptor(
    private val rsaKeyProperties: RsaKeyProperties
): AttributeConverter<String, String> {

    private lateinit var encryptCipher: Cipher
    private lateinit var decryptCipher: Cipher

    @PostConstruct
    fun init() {
        val publicKey = initPublicKey()
        val privateKey = initPrivateKey()

        encryptCipher = Cipher.getInstance("RSA")
            .apply { init(Cipher.ENCRYPT_MODE, publicKey) }
        decryptCipher = Cipher.getInstance("RSA")
            .apply { init(Cipher.DECRYPT_MODE, privateKey) }
    }

    private fun initPublicKey(): PublicKey {
        val publicKeyBytes = Base64.getDecoder().decode(rsaKeyProperties.publicKey)
        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(publicKeySpec)
    }

    private fun initPrivateKey(): PrivateKey {
        val privateKeyBytes = Base64.getDecoder().decode(rsaKeyProperties.privateKey)
        val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec)
    }

    override fun convertToDatabaseColumn(attribute: String?): String? =
        attribute?.let {
            val encryptedBytes = encryptCipher.doFinal(it.toByteArray())
            Base64.getEncoder().encodeToString(encryptedBytes)
        } ?: run { null }


    override fun convertToEntityAttribute(dbData: String?): String? =
        dbData?.let {
            val decryptedBytes = decryptCipher.doFinal(Base64.getDecoder().decode(it))
            String(decryptedBytes)
        } ?: run { null }

}