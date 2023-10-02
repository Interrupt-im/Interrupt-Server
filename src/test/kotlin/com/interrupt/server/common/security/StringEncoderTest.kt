package com.interrupt.server.common.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils

class StringEncoderTest {

    companion object {

        private val stringEncoder: StringEncoder = StringEncoder()

        @BeforeAll
        @JvmStatic
        fun setUpAll() {
            ReflectionTestUtils.setField(stringEncoder, "secretKey", "2b7e151628aed2a6abf7158809cf4f3c")
            ReflectionTestUtils.setField(stringEncoder, "preSalt", "preSalt")
            ReflectionTestUtils.setField(stringEncoder, "postSalt", "postSalt")
        }

    }

    @Test
    fun `주어진 문자열을 암호화`() {
        // given
        val str1 = "testString"
        val str2 = "테스트 문자열"

        // when
        val encryptStr1 = stringEncoder.encrypt(str1)
        val encryptStr2 = stringEncoder.encrypt(str2)

        // then
        assertThat(encryptStr1).isNotEqualTo(str1)
        assertThat(encryptStr2).isNotEqualTo(str2)
    }

    @Test
    fun `주어진 문자열을 복호화`() {
        // given
        val str1 = "v2Xgi4aPkMA50MS8MRFqhCGiyrBqO001itNE7N+GYY0="
        val str2 = "A9cPYgVSvCmlF8yXpAaSd2WDOsEb8ACqkG3jT3Z6npm4awLqA0oIMd1ipYyEM/Wy"

        // when
        val decryptedStr1 = stringEncoder.decrypt(str1)
        val decryptedStr2 = stringEncoder.decrypt(str2)

        // then
        assertThat(decryptedStr1).isNotEqualTo(str1)
        assertThat(decryptedStr2).isNotEqualTo(str2)
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