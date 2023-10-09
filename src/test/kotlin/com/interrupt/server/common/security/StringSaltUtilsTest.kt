package com.interrupt.server.common.security

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class StringSaltUtilsTest {

    @Test
    fun `문자열 앞 뒤로 salt 를 추가한다`() {
        // given
        val preSalt = "preSalt"
        val postSalt = "postSalt"
        val originalStr = "original"

        // when
        val saltAddedStr = addSalt(originalStr, preSalt, postSalt)

        // then
        assertThat(saltAddedStr).isEqualTo("$preSalt$originalStr$postSalt")
    }

    @Test
    fun `문자열 앞 뒤에 있는 salt 를 제거해 반환한다`() {
        // given
        val preSalt = "preSalt"
        val postSalt = "postSalt"
        val originalStr = "original"
        val saltAddedStr = "$preSalt$originalStr$postSalt"

        // when
        val saltRemovedStr = removeSalt(saltAddedStr, preSalt, postSalt)

        // then
        assertThat(saltRemovedStr).isEqualTo(originalStr)
    }

    @Test
    fun `주어진 salt 로 시작하거나 끝나지 않는 문자열에서 salt 를 제거하려고 하는 경우 에러를 던진다`() {
        // given
        val preSalt = "preSalt"
        val postSalt = "postSalt"
        val originalStr = "original"
        val saltAddedStr1 = "1preSalt${originalStr}postSalt"
        val saltAddedStr2 = "preSalt${originalStr}postSalt1"

        // when
        val result1 = assertThatThrownBy { removeSalt(saltAddedStr1, preSalt, postSalt) }
        val result2 = assertThatThrownBy { removeSalt(saltAddedStr2, preSalt, postSalt) }

        // then
        result1.isInstanceOf(InterruptServerException::class.java).hasMessage(ErrorCode.NOT_SALTED_STRING.message)
        result2.isInstanceOf(InterruptServerException::class.java).hasMessage(ErrorCode.NOT_SALTED_STRING.message)
    }

}