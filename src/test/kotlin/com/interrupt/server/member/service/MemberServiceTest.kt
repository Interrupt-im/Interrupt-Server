package com.interrupt.server.member.service

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.member.dto.delete.MemberDeleteRequest
import com.interrupt.server.member.dto.login.MemberLoginRequest
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
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime

class MemberServiceTest {

    companion object {

        private val memberRepository: MemberRepository = mockk<MemberRepository>()
        private val stringEncoder: StringEncoder = mockk<StringEncoder>()
        private val memberService = MemberService(memberRepository, stringEncoder)

        @BeforeAll
        @JvmStatic
        fun setUpAll() {
            MockKAnnotations.init(this)

            ReflectionTestUtils.setField(memberService, "secretKey", "2b7e151628aed2a6abf7158809cf4f3c")
            ReflectionTestUtils.setField(memberService, "idPreSalt", "salt")
            ReflectionTestUtils.setField(memberService, "idPostSalt", "salt")
            ReflectionTestUtils.setField(memberService, "pwPreSalt", "salt")
            ReflectionTestUtils.setField(memberService, "pwPostSalt", "salt")
            ReflectionTestUtils.setField(memberService, "namePreSalt", "salt")
            ReflectionTestUtils.setField(memberService, "namePostSalt", "salt")
            ReflectionTestUtils.setField(memberService, "emailPreSalt", "salt")
            ReflectionTestUtils.setField(memberService, "emailPostSalt", "salt")
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

        every { stringEncoder.encrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(any<String>()) } returns null
        every { memberRepository.save(any<Member>()) } returns
                Member(
                    "salt" + memberRegisterRequest.loginId + "salt",
                    "salt" + memberRegisterRequest.password + "salt",
                    "salt" + memberRegisterRequest.name + "salt",
                    "salt" + memberRegisterRequest.email + "salt"
                )

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

        every { stringEncoder.encrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(any<String>()) } returns mockk<Member>(relaxed = true)
        every { memberRepository.save(any<Member>()) } returns Member(memberRegisterRequest.loginId, memberRegisterRequest.password, memberRegisterRequest.name, memberRegisterRequest.email)

        // when
        val result = assertThatThrownBy { memberService.registerMember(memberRegisterRequest) }

        // then
        result
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.DUPLICATED_REGISTER_LOGIN_ID.message)
    }

    @Test
    fun `회원 ID 와 비밀번호를 전달받아 로그인 로직을 수행한다`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val memberLoginRequest = MemberLoginRequest(loginId, password)

        val savedMemberStub = Member(
            "salt" + loginId + "salt",
            "salt" + password + "salt",
            "testName",
            "test@mail.com"
        )

        every { stringEncoder.encrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }

        every { memberRepository.findByLoginIdAndPassword("salt" + loginId + "salt", "salt" + password + "salt") } returns savedMemberStub

        // when
        val memberLoginResponse = memberService.login(memberLoginRequest)

        // then
        assertThat(memberLoginResponse.loginId).isEqualTo(loginId)
    }

    @Test
    fun `존재하지 않는 회원 ID 와 비밀번호로 로그인을 시도하면 적절한 예외를 반환`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val memberLoginRequest = MemberLoginRequest(loginId, password)

        every { stringEncoder.encrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }

        every { memberRepository.findByLoginIdAndPassword("salt" + loginId + "salt", "salt" + password + "salt") } returns null

        // when
        val result = assertThatThrownBy { memberService.login(memberLoginRequest) }

        // then
        result.isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.MEMBER_NOT_FOUND.message)
    }

    @Test
    fun `회원 정보를 받아 해당되는 회원 탈퇴 비지니스 로직을 수행한다`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val memberDeleteRequest = MemberDeleteRequest(loginId, password)

        val savedMemberStub = Member(
            "salt" + loginId + "salt",
            "salt" + password + "salt",
            "testName",
            "test@mail.com"
        )

        every { stringEncoder.encrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }

        every { memberRepository.findByLoginIdAndPassword("salt" + loginId + "salt", "salt" + password + "salt") } returns savedMemberStub
        every { memberRepository.save(any<Member>()) } returns savedMemberStub.also { it.deletedAt = LocalDateTime.now() }

        // when
        val memberDeleteResponse = memberService.deleteMember(memberDeleteRequest)

        // then
        assertThat(memberDeleteResponse.loginId).isEqualTo(loginId)
    }

    @Test
    fun `존재하지 않는 회원 ID 와 비밀번호로 회원탈퇴를 시도하면 적절한 예외를 반환`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val memberDeleteRequest = MemberDeleteRequest(loginId, password)

        every { stringEncoder.encrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>(), any<String>()) } answers { it.invocation.args[0] as String }

        every { memberRepository.findByLoginIdAndPassword("salt" + loginId + "salt", "salt" + password + "salt") } returns null

        // when
        val result = assertThatThrownBy { memberService.deleteMember(memberDeleteRequest) }

        // then
        result.isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.MEMBER_NOT_FOUND.message)
    }

}