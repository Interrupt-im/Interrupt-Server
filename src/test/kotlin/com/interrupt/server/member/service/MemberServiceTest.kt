package com.interrupt.server.member.service

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.email.dto.EmailType
import com.interrupt.server.email.entity.EmailMessage
import com.interrupt.server.email.service.EmailSendService
import com.interrupt.server.member.dto.delete.MemberDeleteRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyRequest
import com.interrupt.server.member.dto.emailverify.EmailVerifyRequest
import com.interrupt.server.member.dto.recover.RecoverLoginIdRequest
import com.interrupt.server.member.dto.recover.RecoverPasswordRequest
import com.interrupt.server.member.dto.recover.VerifyRecoverLoginIdRequest
import com.interrupt.server.member.dto.recover.VerifyRecoverPasswordRequest
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.dto.update.MemberUpdateRequest
import com.interrupt.server.member.entity.EmailVerifyCode
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.entity.MemberRecover
import com.interrupt.server.member.repository.EmailVerifyCodeRepository
import com.interrupt.server.member.repository.MemberRecoverRepository
import com.interrupt.server.member.repository.MemberRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class MemberServiceTest {

    companion object {

        private val memberRepository: MemberRepository = mockk<MemberRepository>()
        private val emailSendService: EmailSendService = mockk<EmailSendService>()
        private val emailVerifyCodeRepository: EmailVerifyCodeRepository = mockk<EmailVerifyCodeRepository>()
        private val memberRecoverRepository: MemberRecoverRepository = mockk<MemberRecoverRepository>()
        private val stringEncoder: StringEncoder = mockk<StringEncoder>()
        private val memberService = MemberService(memberRepository, emailSendService, emailVerifyCodeRepository, memberRecoverRepository, stringEncoder)

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
        val memberRegisterRequest = MemberRegisterRequest("test1", "testPassword", "testName", "test@mail.com", "0000")
        val verifyCode = EmailVerifyCode(memberRegisterRequest.email!!, "000000", true).apply { uuid = memberRegisterRequest.emailVerifyCodeKey!! }

        every { emailVerifyCodeRepository.findByUuid(any<String>()) } returns verifyCode
        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(any<String>()) } returns null
        every { memberRepository.save(any<Member>()) } returns
                Member(
                    memberRegisterRequest.loginId!!,
                    memberRegisterRequest.password!!,
                    memberRegisterRequest.name!!,
                    memberRegisterRequest.email!!,
                )

        // when then
        memberService.registerMember(memberRegisterRequest)
    }

    @Test
    fun `인증이 완료되지 않은 이메일 주소 로 회원 가입을 시도할 때 적절한 예외를 반환한다`() {
        // given
        val memberRegisterRequest = MemberRegisterRequest("test1", "testPassword", "testName", "test@mail.com", "0000")
        val verifyCode = EmailVerifyCode(memberRegisterRequest.email!!, "000000", false).apply { uuid = memberRegisterRequest.emailVerifyCodeKey!! }

        every { emailVerifyCodeRepository.findByUuid(any<String>()) } returns verifyCode
        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(any<String>()) } returns mockk<Member>(relaxed = true)
        every { memberRepository.save(any<Member>()) } returns
                Member(
                    memberRegisterRequest.loginId!!,
                    memberRegisterRequest.password!!,
                    memberRegisterRequest.name!!,
                    memberRegisterRequest.email!!,
                )

        // when
        val result = assertThatThrownBy { memberService.registerMember(memberRegisterRequest) }

        // then
        result
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.EMAIL_NOT_VERIFIED.message)
    }

    @Test
    fun `인증 요청 내역이 없는 이메일 주소 로 회원 가입을 시도할 때 적절한 예외를 반환한다`() {
        // given
        val memberRegisterRequest = MemberRegisterRequest("test1", "testPassword", "testName", "test@mail.com", "0000")

        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { emailVerifyCodeRepository.findByUuid(any<String>()) } returns null

        // when
        val result = assertThatThrownBy { memberService.registerMember(memberRegisterRequest) }

        // then
        result
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND.message)
    }

    @Test
    fun `이미 존재하는 ID 로 회원 가입을 시도할 때 적절한 에러를 발생 시키는 단위 테스트`() {
        // given
        val memberRegisterRequest = MemberRegisterRequest("test1", "testPassword", "testName", "test@mail.com", "0000")
        val verifyCode = EmailVerifyCode(memberRegisterRequest.email!!, "000000", true).apply { uuid = memberRegisterRequest.emailVerifyCodeKey!! }

        every { emailVerifyCodeRepository.findByUuid(any<String>()) } returns verifyCode
        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(any<String>()) } returns mockk<Member>(relaxed = true)
        every { memberRepository.save(any<Member>()) } returns
                Member(
                    memberRegisterRequest.loginId!!,
                    memberRegisterRequest.password!!,
                    memberRegisterRequest.name!!,
                    memberRegisterRequest.email!!,
                    )

        // when
        val result = assertThatThrownBy { memberService.registerMember(memberRegisterRequest) }

        // then
        result
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.DUPLICATED_REGISTER_LOGIN_ID.message)
    }

    @Test
    fun `중복된 ID 가 없는 회원 ID 에 대한 중복체크를 수행 후 중복 여부를 반환한다`() {
        // given
        val loginId = "testId"
        val request = LoginIdDuplicateCheckRequest(loginId)

        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(any<String>()) } returns null

        // when
        val response = memberService.checkLoginIdDuplication(request)

        // then
        assertThat(response.isUnique).isTrue()
    }

    @Test
    fun `중복된 ID 가 존재하는 회원 ID 에 대한 중복체크를 수행 후 중복 여부를 반환한다`() {
        // given
        val loginId = "testId"
        val request = LoginIdDuplicateCheckRequest(loginId)

        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(any<String>()) } returns Member(loginId, "testPw", "testName", "test@test.com")

        // when
        val response = memberService.checkLoginIdDuplication(request)

        // then
        assertThat(response.isUnique).isFalse()
    }

    @Test
    fun `이메일 인증코드 발송 후 이메일 인증코드 키값을 반환한다`() {
        // given
        val request = EmailVerificationApplyRequest("test@test.com")

        every { emailSendService.generateEmailTemplate(EmailType.MEMBER_REGISTER.template, any<Map<String, Any>>()) } returns "contents"
        justRun { emailSendService.sendMail(any<EmailMessage>()) }
        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { emailVerifyCodeRepository.save(any<EmailVerifyCode>()) } answers { (it.invocation.args[0] as EmailVerifyCode).apply { uuid = "emailVerifyCodeKey" } }

        // when
        val response = memberService.applySendEmailVerifyCode(request)

        // then
        assertThat(response.emailVerifyCodeKey).isNotNull()
    }

    @Test
    fun `이메일 인증코드 키를 통해 인증코드 검증 후 검증 결과를 반환한다`() {
        // given
        val email = "test@test.com"
        val emailVerifyCodeKey = "0000"
        val verifyCode = "000000"

        every { emailVerifyCodeRepository.findByUuid(emailVerifyCodeKey) } returns EmailVerifyCode(email, verifyCode, false)
        every { emailVerifyCodeRepository.save(any<EmailVerifyCode>()) } answers { it.invocation.args[0] as EmailVerifyCode }

        // when then
        memberService.validateEmailVerifyCode(EmailVerifyRequest(email, emailVerifyCodeKey, verifyCode))

    }

    @Test
    fun `올바르지 않은 이메일 인증코드를 입력한 경우 예외를 반환한다`() {
        // given
        val email = "test@test.com"
        val emailVerifyCodeKey = "0000"
        val verifyCode = "000000"
        val wrongVerifyCode = "111111"

        every { emailVerifyCodeRepository.findByUuid(emailVerifyCodeKey) } returns EmailVerifyCode(email, wrongVerifyCode, false)

        // when
        val response = assertThatThrownBy { memberService.validateEmailVerifyCode(EmailVerifyRequest(email, emailVerifyCodeKey, verifyCode)) }

        // then
        response
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.INVALID_EMAIL_VERIFY_CODE.message)
    }

    @Test
    fun `이메일 인증코드 키 값에 대한 인증코드가 없는 경우 예외를 반환한다`() {
        // given
        val emailVerifyCodeKey = "0000"
        val verifyCode = "000000"

        every { emailVerifyCodeRepository.findByUuid(emailVerifyCodeKey) } returns null

        // when
        val response = assertThatThrownBy { memberService.validateEmailVerifyCode(EmailVerifyRequest("test@test.com", emailVerifyCodeKey, verifyCode)) }

        // then
        response
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND.message)
    }

    @Test
    fun `회원 정보를 받아 해당되는 회원 탈퇴 비지니스 로직을 수행한다`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val memberDeleteRequest = MemberDeleteRequest(password)

        val savedMemberStub = Member(
            loginId,
            password,
            "testName",
            "test@mail.com"
        )

        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>()) } answers { it.invocation.args[0] as String }

        every { memberRepository.findByLoginIdAndLoginPassword(loginId, password) } returns savedMemberStub
        every { memberRepository.save(any<Member>()) } returns savedMemberStub.also { it.deletedAt = LocalDateTime.now() }

        // when then
        memberService.deleteMember(loginId, memberDeleteRequest)
    }

    @Test
    fun `존재하지 않는 회원 ID 와 비밀번호로 회원탈퇴를 시도하면 적절한 예외를 반환`() {
        // given
        val loginId = "test1"
        val password = "testPassword"
        val memberDeleteRequest = MemberDeleteRequest(password)

        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { stringEncoder.decrypt(any<String>()) } answers { it.invocation.args[0] as String }

        every { memberRepository.findByLoginIdAndLoginPassword(loginId, password) } returns null

        // when
        val result = assertThatThrownBy { memberService.deleteMember(loginId, memberDeleteRequest) }

        // then
        result.isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.MEMBER_NOT_FOUND.message)
    }

    @Test
    fun `회원 ID 찾기 이메일 인증코드를 발송 후 이메일 인증코드 키값을 반환한다`() {
        // given
        val loginId = "testLoginId"
        val userName = "testName"
        val userEmail = "test@test.com"
        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByNameAndEmail(any<String>(), any<String>()) } returns Member(loginId, "password", userName, userEmail)
        every { emailSendService.generateEmailTemplate(EmailType.LOGIN_ID_RECOVER.template, any<Map<String, Any>>()) } returns "contents"
        justRun { emailSendService.sendMail(any<EmailMessage>()) }
        every { memberRecoverRepository.save(any<MemberRecover>()) } answers { (it.invocation.args[0] as MemberRecover).apply { uuid = "emailVerifyCodeKey" } }

        // when
        val response = memberService.applySendLoginIdRecoverVerifyCode(RecoverLoginIdRequest(userName, userEmail))

        // then
        assertThat(response.memberRecoverKey).isNotNull()
    }

    @Test
    fun `회원 ID 찾기 이메일 인증코드를 발송할 때 이름과 이메일이 일치하는 회원이 없는 경우 예외를 반환한다`() {
        // given
        val userName = "testName"
        val userEmail = "test@test.com"
        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByNameAndEmail(any<String>(), any<String>()) } returns null

        // when
        val response = assertThatThrownBy{ memberService.applySendLoginIdRecoverVerifyCode(RecoverLoginIdRequest(userName, userEmail)) }

        // then
        response
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.MEMBER_NOT_FOUND.message)
    }

    @Test
    fun `회원 ID 찾기 메일 인증코드를 검증한다`() {
        // given
        val memberRecoverKey = "0000"
        val verifyCode = "000000"
        val loginId = "testLoginId"

        every { memberRecoverRepository.findByUuid(memberRecoverKey) } returns MemberRecover("test@test.com", loginId, verifyCode)
        every { stringEncoder.decrypt(any<String>()) } answers { it.invocation.args[0] as String }

        // when
        val response = memberService.validateLoginIdRecoverVerifyCode(VerifyRecoverLoginIdRequest(memberRecoverKey, verifyCode))

        // then
        assertThat(response.loginId).isEqualTo(loginId)
    }

    @Test
    fun `회원 ID 찾기 메일 인증코드 키 값에 대한 인증코드가 없는 경우 예외를 반환한다`() {
        // given
        val memberRecoverKey = "0000"
        val verifyCode = "000000"
        val loginId = "testLoginId"

        every { memberRecoverRepository.findByUuid(memberRecoverKey) } returns null

        // when
        val response = assertThatThrownBy { memberService.validateLoginIdRecoverVerifyCode(VerifyRecoverLoginIdRequest(memberRecoverKey, verifyCode)) }

        // then
        response
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND.message)
    }

    @Test
    fun `회원 ID 찾기 메일 인증코드 키 값에 대한 인증코드와 검증하는 인증코드가 다른 경우 예외를 반환한다`() {
        // given
        val memberRecoverKey = "0000"
        val verifyCode = "000000"
        val loginId = "testLoginId"
        val wrongVerifyCode = "111111"

        every { memberRecoverRepository.findByUuid(memberRecoverKey) } returns MemberRecover("test@test.com", loginId, wrongVerifyCode)

        // when
        val response = assertThatThrownBy { memberService.validateLoginIdRecoverVerifyCode(VerifyRecoverLoginIdRequest(memberRecoverKey, verifyCode)) }

        // then
        response
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.INVALID_EMAIL_VERIFY_CODE.message)
    }

    @Test
    fun `회원 비밀번호 찾기 메일 인증코드를 발송 후 이메일 인증코드 키값을 반환한다`() {
        // given
        val request = RecoverPasswordRequest("testLoginId", "test@test.com")

        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginIdAndEmail(any<String>(), any<String>()) } returns Member(request.loginId!!, "password", "name", request.email!!)
        every { emailSendService.generateEmailTemplate(EmailType.PASSWORD_RECOVER.template, any<Map<String, Any>>()) } returns "contents"
        justRun { emailSendService.sendMail(any<EmailMessage>()) }
        every { memberRecoverRepository.save(any<MemberRecover>()) } answers { (it.invocation.args[0] as MemberRecover).apply { uuid = "emailVerifyCodeKey" } }

        // when
        val response = memberService.applySendPasswordRecoverVerifyCode(request)

        // then
        assertThat(response.memberRecoverKey).isNotNull()
    }

    @Test
    fun `회원 비밀번호 찾기 이메일 인증코드를 발송할 때 ID 와 이메일이 일치하는 회원이 없는 경우 예외를 반환한다`() {
        // given
        val loginId = "testLoginId"
        val userEmail = "test@test.com"
        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginIdAndEmail(any<String>(), any<String>()) } returns null

        // when
        val response = assertThatThrownBy{ memberService.applySendPasswordRecoverVerifyCode(RecoverPasswordRequest(loginId, userEmail)) }

        // then
        response
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.MEMBER_NOT_FOUND.message)
    }

    @Test
    fun `회원 비밀번호 찾기 메일 인증코드를 검증한다`() {
        // given
        val memberRecoverKey = "0000"
        val verifyCode = "000000"
        val loginId = "testLoginId"
        val newPassword = "newPassword"
        val memberStub = Member(loginId, "password", "name", "test@test.com")

        every { memberRecoverRepository.findByUuid(memberRecoverKey) } returns MemberRecover("test@test.com", loginId, verifyCode)
        every { stringEncoder.decrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { memberRepository.findByLoginId(loginId) } returns memberStub
        every { memberRepository.save(memberStub.apply { loginPassword = newPassword }) } answers { it.invocation.args[0] as Member }

        // when then
        memberService.validatePasswordRecoverVerifyCode(VerifyRecoverPasswordRequest(memberRecoverKey, verifyCode, newPassword))
    }

    @Test
    fun `회원 비밀번호 찾기 메일 인증코드 키 값에 대한 인증코드가 없는 경우 예외를 반환한다`() {
        // given
        val memberRecoverKey = "0000"
        val verifyCode = "000000"
        val newPassword = "newPassword"

        every { memberRecoverRepository.findByUuid(memberRecoverKey) } returns null

        // when
        val response = assertThatThrownBy { memberService.validatePasswordRecoverVerifyCode(VerifyRecoverPasswordRequest(memberRecoverKey, verifyCode, newPassword)) }

        // then
        response
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND.message)
    }

    @Test
    fun `회원 비밀번호 찾기 메일 인증코드 키 값에 대한 인증코드와 검증하는 인증코드가 다른 경우 예외를 반환한다`() {
        // given
        val memberRecoverKey = "0000"
        val verifyCode = "000000"
        val wrongVerifyCode = "111111"
        val loginId = "testLoginId"
        val newPassword = "newPassword"

        every { memberRecoverRepository.findByUuid(memberRecoverKey) } returns MemberRecover("test@test.com", loginId, wrongVerifyCode)

        // when
        val response = assertThatThrownBy { memberService.validatePasswordRecoverVerifyCode(VerifyRecoverPasswordRequest(memberRecoverKey, verifyCode, newPassword)) }

        // then
        response
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.INVALID_EMAIL_VERIFY_CODE.message)
    }

    @Test
    fun `회원 ID 를 이용해 회원 엔티티를 찾고 해당 엔티티에 대해 회원 정보 수정을 수행한다`() {
        // given
        val loginId = "loginId"
        val newPassword = "newPassword"
        val newEmail = "newTest@test.com"
        val memberStub = Member(loginId, "password", "name", "test@test.com")
        val emailVerifyCodeKey = "0000"

        every { memberRepository.findByLoginId(loginId) } returns memberStub
        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { emailVerifyCodeRepository.findByUuid(emailVerifyCodeKey) } returns EmailVerifyCode(newEmail, "000000", true)

        every { memberRepository.save(memberStub.also {
            it.loginPassword = newPassword
            it.email = newEmail
        }) } answers { it.invocation.args[0] as Member }

        // when then
        val request = MemberUpdateRequest(newPassword, email = newEmail, emailVerifyCodeKey = emailVerifyCodeKey)
        memberService.updateMember(loginId, request)
    }

    @Test
    fun `회원정보를 수정할 때 회원 ID 에 해당하는 회원 엔티티가 없으면 예외를 반환한다`() {
        // given
        val loginId = "loginId"

        every { memberRepository.findByLoginId(loginId) } returns null

        // when then
        assertThatThrownBy { memberService.updateMember(loginId, MemberUpdateRequest(emailVerifyCodeKey = "0000")) }
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.MEMBER_NOT_FOUND.message)
    }

    @Test
    fun `회원정보를 수정할 때 변경하고자 하는 email 인증 요청 내역이 없거나 인증을 받지 않은 경우 예외를 반환한다`() {
        // given
        val loginId = "loginId"
        val newEmail1 = "newTest1@test.com"
        val newEmail2 = "newTest2@test.com"
        val memberStub = Member(loginId, "password", "name", "test@test.com")
        val emailVerifyCodeKey = "0000"
        val emailVerifyCodeKey2 = "1111"

        every { memberRepository.findByLoginId(loginId) } returns memberStub
        every { stringEncoder.encrypt(any<String>()) } answers { it.invocation.args[0] as String }
        every { emailVerifyCodeRepository.findByUuid(emailVerifyCodeKey) } returns EmailVerifyCode(newEmail1, emailVerifyCodeKey, false)
        every { emailVerifyCodeRepository.findByUuid(emailVerifyCodeKey2) } returns null

        // when then
        assertThatThrownBy { memberService.updateMember(loginId, MemberUpdateRequest(email = newEmail1, emailVerifyCodeKey = emailVerifyCodeKey)) }
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.EMAIL_NOT_VERIFIED.message)
        assertThatThrownBy { memberService.updateMember(loginId, MemberUpdateRequest(email = newEmail2, emailVerifyCodeKey = emailVerifyCodeKey2)) }
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND.message)
    }

}