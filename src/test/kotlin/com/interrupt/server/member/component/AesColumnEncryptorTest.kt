package com.interrupt.server.member.component

import com.interrupt.server.auth.config.AesKeyProperties
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AesColumnEncryptorTest {

    private val aesKeyProperties: AesKeyProperties = mockk<AesKeyProperties>().also {
        every { it.secretKey } returns "B/7aN1ds1rgdPH+6cv8Kd3XP/0m/PBxRiTV2vL7cpl0="
        every { it.iv } returns "wpkVFdWbfKrqa+rK4DvPiA=="
        every { it.algorithm } returns "AES"
        every { it.transformation } returns "AES/CBC/PKCS5Padding"
    }

    private val aesColumnEncryptor = AesColumnEncryptor(aesKeyProperties)

    @BeforeEach
    fun setUp() {
        aesColumnEncryptor.init()
    }

    @Test
    fun `문자열을 RSA 암호화 한다`() {
        // given
        val original = "test"

        // when
        val encrypted = aesColumnEncryptor.convertToDatabaseColumn(original)

        // then
        assertThat(encrypted).isNotEqualTo(original)
    }

    @Test
    fun `RSA 암호화를 할 때, null 값을 받으면 그대로 null 을 반환한다`() {
        // given
        val original = null

        // when
        val encrypted = aesColumnEncryptor.convertToDatabaseColumn(original)

        // then
        assertThat(encrypted).isNull()
    }

    @Test
    fun `암호화 된 문자열을 RSA 복호화 한다`() {
        // given
        val encrypted = "k0buUlx3TfFbVtCUkgJDRA=="

        // when
        val decrypted = aesColumnEncryptor.convertToEntityAttribute(encrypted)

        // then
        assertThat(decrypted).isEqualTo("test")
    }

    @Test
    fun `RSA 복호화를 할 때, null 을 받으면 그대로 null 을 반환한다`() {
        // given
        val encrypted = null

        // when
        val decrypted = aesColumnEncryptor.convertToEntityAttribute(encrypted)

        // then
        assertThat(decrypted).isNull()
    }

}