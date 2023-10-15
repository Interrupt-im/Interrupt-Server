package com.interrupt.server.common.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StringEncoderTest {

    private val stringEncoder: StringEncoder = StringEncoder()

    private val secretKey = "2b7e151628aed2a6abf7158809cf4f3c"

    @Test
    fun `주어진 문자열을 암호화`() {
        // given
        val str1 = "testString"
        val str2 = "테스트 문자열"

        // when
        val encryptStr1 = stringEncoder.encrypt(str1)
        val encryptStr2 = stringEncoder.encrypt(str2)

        // then
        assertThat(encryptStr1).isEqualTo("ZIwG/E8s+H9P8FQpsMM+Bw==")
        assertThat(encryptStr2).isEqualTo("9w34K9euYplUfRTGEExDjqNj6UVwc4Yt/3WsMOCkCe0=")
    }

    @Test
    fun `주어진 문자열을 복호화`() {
        // given
        val str1 = "ZIwG/E8s+H9P8FQpsMM+Bw=="
        val str2 = "9w34K9euYplUfRTGEExDjqNj6UVwc4Yt/3WsMOCkCe0="

        // when
        val decryptedStr1 = stringEncoder.decrypt(str1)
        val decryptedStr2 = stringEncoder.decrypt(str2)

        // then
        assertThat(decryptedStr1).isEqualTo("testString")
        assertThat(decryptedStr2).isEqualTo("테스트 문자열")
    }

    @Test
    fun `주어진 문자열을 암호화 후 복호화를 하면 동일한 문자열이 반환된다`() {
        // given
        val str1 = "testString"
        val str2 = "테스트 문자열"
        val encryptStr1 = stringEncoder.encrypt(str1)
        val encryptStr2 = stringEncoder.encrypt(str2)

        // when
        val decryptStr1 = stringEncoder.decrypt(encryptStr1)
        val decryptStr2 = stringEncoder.decrypt(encryptStr2)

        // then
        assertThat(decryptStr1).isEqualTo(str1)
        assertThat(decryptStr2).isEqualTo(str2)
    }

}