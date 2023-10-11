package com.interrupt.server.member.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.member.dto.delete.MemberDeleteRequest
import com.interrupt.server.member.dto.login.MemberLoginRequest
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
            .hasMessage(ErrorCode.DUPLICATED_REGISTER_LOGIN_ID.message)
    }

    @Test
    fun `회원 ID 와 비밀번호를 전달받아 로그인 로직을 수행 후 회원 이름을 반환한다`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val name = "testName"
        val memberRegisterRequest = MemberRegisterRequest(loginId, password, name, "test@mail.com")
        val memberLoginRequest = MemberLoginRequest(loginId, password)

        memberService.registerMember(memberRegisterRequest)

        // when
        val memberLoginResponse = memberService.login(memberLoginRequest)

        // then
        assertThat(memberLoginResponse.name).isEqualTo(name)
    }

    @Test
    fun `존재하지 않는 회원 ID 와 비밀번호로 로그인을 시도하면 적절한 예외를 반환`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val memberLoginRequest = MemberLoginRequest(loginId, password)

        // when
        val result = assertThatThrownBy { memberService.login(memberLoginRequest) }

        // then
        result.isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.FAILED_LOGIN.message)
    }

    @Test
    fun `회원 정보를 받아 해당되는 회원 탈퇴 비지니스 로직을 수행한다`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val memberRegisterRequest = MemberRegisterRequest(loginId, password, "testName", "test@mail.com")
        val memberDeleteRequest = MemberDeleteRequest(loginId, password)

        memberService.registerMember(memberRegisterRequest)

        // when then
        memberService.deleteMember(memberDeleteRequest)

    }

    @Test
    fun `존재하지 않는 회원 ID 와 비밀번호로 회원탈퇴를 시도하면 적절한 예외를 반환`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val memberDeleteRequest = MemberDeleteRequest(loginId, password)

        // when
        val result = assertThatThrownBy { memberService.deleteMember(memberDeleteRequest) }

        // then
        result.isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.FAILED_LOGIN.message)
    }

}