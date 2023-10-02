package com.interrupt.server.member.service

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class MemberServiceTest {

    private val memberRepository: MemberRepository = mockk<MemberRepository>()
    private val stringEncoder: StringEncoder = mockk<StringEncoder>()
    private val memberService = MemberService(memberRepository, stringEncoder)

    companion object {

        @BeforeAll
        @JvmStatic
        fun setUpAll() {
            MockKAnnotations.init(this)
        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {
            clearAllMocks()
        }

    }

    @Test
    fun `회원 정보를 받아 회원 가입을 하는 단위 테스트`() {
        // given
        val memberRegisterRequest = MemberRegisterRequest("test1", "testPassword", "testName", "test@mail.com")

        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(any<String>()) } returns null
        every { memberRepository.save(any<Member>()) } returns Member(memberRegisterRequest.loginId, memberRegisterRequest.password, memberRegisterRequest.name, memberRegisterRequest.email)

        // when
        val savedMember = memberService.registerMember(memberRegisterRequest)

        // then
        assertThat(savedMember)
            .extracting("loginId", "name", "email")
            .contains(memberRegisterRequest.loginId, memberRegisterRequest.name, memberRegisterRequest.email)
    }

    @Test
    fun `이미 존재하는 ID 로 회원 가입을 시도할 때 적절한 에러를 발생 시키는 단위 테스트`() {
        // given
        val memberRegisterRequest = MemberRegisterRequest("test1", "testPassword", "testName", "test@mail.com")

        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(any<String>()) } returns mockk<Member>(relaxed = true)
        every { memberRepository.save(any<Member>()) } returns Member(memberRegisterRequest.loginId, memberRegisterRequest.password, memberRegisterRequest.name, memberRegisterRequest.email)

        // when
        val result = assertThatThrownBy { memberService.registerMember(memberRegisterRequest) }

        // then
        result
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.DUPLICATED_LOGIN_ID.message)
    }
}