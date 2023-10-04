package com.interrupt.server.member.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional
class MemberServiceIntegrationTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var memberService: MemberService

    @Test
    fun `회원 가입 dto 를 통해 받은 정보로 회원 가입을 수행 한다`() {
        // given
        val testMemberDto = MemberRegisterRequest("test1", "testPassword", "testName", "test@mail.com")

        // when
        val savedMember = memberService.registerMember(testMemberDto)

        // then
        assertThat(savedMember)
            .isNotNull
            .extracting("loginId", "name", "email")
            .contains(testMemberDto.loginId, testMemberDto.name, testMemberDto.email)
    }

    @Test
    fun `이미 존재하는 ID 로 회원가입을 시도하면 그에 맞는 에러를 발생한다`() {
        // given
        val testMemberDto1 = MemberRegisterRequest("test1", "testPassword", "testName1", "test1@mail.com")
        val testMemberDto2 = MemberRegisterRequest("test1", "testPassword", "testName2", "test2@mail.com")

        memberService.registerMember(testMemberDto1)

        // when
        val result = assertThatThrownBy { memberService.registerMember(testMemberDto2) }

        // then
        result
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.DUPLICATED_LOGIN_ID.message)

    }

}