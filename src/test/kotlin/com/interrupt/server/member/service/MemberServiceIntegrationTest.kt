package com.interrupt.server.member.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.email.dto.EmailContent
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
import com.ninjasquad.springmockk.SpykBean
import io.mockk.justRun
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional
class MemberServiceIntegrationTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var memberRepository: MemberRepository
    @SpykBean
    private lateinit var emailSendService: EmailSendService
    @Autowired
    private lateinit var emailVerifyCodeRepository: EmailVerifyCodeRepository
    @Autowired
    private lateinit var memberRecoverRepository: MemberRecoverRepository
    @Autowired
    private lateinit var stringEncoder: StringEncoder

    @Test
    fun `회원 가입 dto 를 통해 받은 정보로 회원 가입을 수행 한다`() {
        // given
        val email = "test@mail.com"
        val encryptedEmail = stringEncoder.encrypt(email)

        val verifyCode = emailVerifyCodeRepository.save(EmailVerifyCode(encryptedEmail, "000000", true))
        val testMemberDto = MemberRegisterRequest("test1", "testPassword", "testName", email, verifyCode.uuid)

        // when then
        memberService.registerMember(testMemberDto)
    }

    @Test
    fun `아이디 중복체크 요청을 받아 중복체크를 수행한다`() {
        // given
        val request = LoginIdDuplicateCheckRequest("test")

        // when
        val response = memberService.checkLoginIdDuplication(request)

        // then
        assertThat(response.isUnique).isTrue()
    }

    @Test
    fun `이메일 인증 코드 발송 요청을 받아 인증코드를 발송한다`() {
        // given
        val email = "test@test.com"
        val request = EmailVerificationApplyRequest(email)

        justRun { emailSendService.sendMail(EmailMessage(email, EmailContent(EmailType.MEMBER_REGISTER.subject, "testContent"))) }

        // when
        val response = memberService.applySendEmailVerifyCode(request)

        // then
        assertThat(response.emailVerifyCodeKey).isNotNull()
    }

    @Test
    fun `입력한 이메일 인증코드가 올바른지 검증한다`() {
        // given
        val email = "test@mail.com"
        val encryptedEmail = stringEncoder.encrypt(email)
        val verifyCode = "000000"
        val verifyCodeEntity = emailVerifyCodeRepository.save(EmailVerifyCode(encryptedEmail, verifyCode, false))

        val request = EmailVerifyRequest(verifyCodeEntity.uuid, verifyCodeEntity.uuid, verifyCode)

        // when
        memberService.validateEmailVerifyCode(request)

        // then
        val result = emailVerifyCodeRepository.findByUuid(verifyCodeEntity.uuid)
        assertThat(result).isNotNull
            .extracting("email", "verifyCode", "isVerified")
            .contains(encryptedEmail, verifyCode, true)

    }

    @Test
    fun `회원 탈퇴를 수행한다`() {
        // given
        val loginId = "loginId"
        val password = "password"
        val name = "name"
        val email = "test@test.com"
        val encryptedLoginId = stringEncoder.encrypt(loginId)
        val encryptedPassword = stringEncoder.encrypt(password)
        val encryptedName = stringEncoder.encrypt(name)
        val savedMember = memberRepository.save(Member(encryptedLoginId, encryptedPassword, encryptedName, email))

        val request = MemberDeleteRequest(password)

        // when
        memberService.deleteMember(loginId, request)

        // then
        assertThat(savedMember.deletedAt)
            .isNotNull()
//            .isBefore(LocalDateTime.now())
    }

    @Test
    fun `회원 ID 찾기를 위한 이메일 인증코드를 발송한다`() {
        // given
        val loginId = "loginId"
        val password = "password"
        val name = "홍길동"
        val email = "test@test.com"
        val encryptedLoginId = stringEncoder.encrypt(loginId)
        val encryptedPassword = stringEncoder.encrypt(password)
        val encryptedName = stringEncoder.encrypt(name)
        val encryptedEmail = stringEncoder.encrypt(email)
        memberRepository.save(Member(encryptedLoginId, encryptedPassword, encryptedName, encryptedEmail))

        justRun { emailSendService.sendMail(EmailMessage(email, EmailContent(EmailType.LOGIN_ID_RECOVER.subject, "testContent"))) }

        val request = RecoverLoginIdRequest(name, email)

        // when
        val response = memberService.applySendLoginIdRecoverVerifyCode(request)

        // then
        assertThat(response.memberRecoverKey)
    }

    @Test
    fun `회원 ID 찾기를 위한 이메일 인증코드를 검증한다`() {
        // given
        val email = "test@test.com"
        val loginId = "loginId"
        val verifyCode = "000000"
        val encryptedLoginId = stringEncoder.encrypt(loginId)
        val verifyCodeEntity = memberRecoverRepository.save(MemberRecover(email, encryptedLoginId, verifyCode))

        val request = VerifyRecoverLoginIdRequest(verifyCodeEntity.uuid, verifyCode)

        // when
        val response = memberService.validateLoginIdRecoverVerifyCode(request)

        // then
        assertThat(response.loginId).isEqualTo(loginId)
    }

    @Test
    fun `회원 비밀번호 찾기를 위한 이메일 인증코드를 발송한다`() {
        // given
        val loginId = "loginId"
        val password = "password"
        val name = "홍길동"
        val email = "test@test.com"
        val encryptedLoginId = stringEncoder.encrypt(loginId)
        val encryptedPassword = stringEncoder.encrypt(password)
        val encryptedName = stringEncoder.encrypt(name)
        val encryptedEmail = stringEncoder.encrypt(email)
        memberRepository.save(Member(encryptedLoginId, encryptedPassword, encryptedName, encryptedEmail))

        justRun { emailSendService.sendMail(EmailMessage(email, EmailContent(EmailType.PASSWORD_RECOVER.subject, "testContent"))) }

        val request = RecoverPasswordRequest(loginId, email)

        // when
        val response = memberService.applySendPasswordRecoverVerifyCode(request)

        // then
        assertThat(response.memberRecoverKey).isNotNull()
    }

    @Test
    fun `회원 비밀번호 찾기를 위한 이메일 인증코드를 검증한다`() {
        // given
        val loginId = "loginId"
        val password = "password"
        val name = "홍길동"
        val email = "test@test.com"
        val encryptedLoginId = stringEncoder.encrypt(loginId)
        val encryptedPassword = stringEncoder.encrypt(password)
        val encryptedName = stringEncoder.encrypt(name)
        val encryptedEmail = stringEncoder.encrypt(email)

        val savedMember = memberRepository.save(Member(encryptedLoginId, encryptedPassword, encryptedName, encryptedEmail))

        val verifyCode = "000000"
        val verifyCodeEntity = memberRecoverRepository.save(MemberRecover(email, encryptedLoginId, verifyCode))

        val newPassword = "newPassword"
        val request = VerifyRecoverPasswordRequest(verifyCodeEntity.uuid, verifyCode, newPassword)

        // when
        memberService.validatePasswordRecoverVerifyCode(request)

        // then
        assertThat(savedMember.loginPassword).isEqualTo(stringEncoder.encrypt(newPassword))
    }

    @Test
    fun `회원 정보를 수정한다`() {
        // given
        val loginId = "loginId"
        val password = "password"
        val name = "홍길동"
        val email = "test@test.com"
        val encryptedLoginId = stringEncoder.encrypt(loginId)
        val encryptedPassword = stringEncoder.encrypt(password)
        val encryptedName = stringEncoder.encrypt(name)
        val encryptedEmail = stringEncoder.encrypt(email)
        val member = Member(encryptedLoginId, encryptedPassword, encryptedName, encryptedEmail)
        memberRepository.save(member)

        val newPassword = "newPassword"
        val newName = "김철수"
        val newEmail = "new@test.com"

        val encryptedNewPassword = stringEncoder.encrypt(newPassword)
        val encryptedNewName = stringEncoder.encrypt(newName)
        val encryptedNewEmail = stringEncoder.encrypt(newEmail)

        val emailVerifyCode = EmailVerifyCode(encryptedNewEmail, "000000", true)
        emailVerifyCodeRepository.save(emailVerifyCode)

        val request = MemberUpdateRequest(newPassword, newName, newEmail, emailVerifyCode.uuid)

        // when
        memberService.updateMember(loginId, request)

        // then
        val updatedMember = memberRepository.findByLoginId(encryptedLoginId)
        assertThat(updatedMember)
            .isNotNull
            .extracting("loginId", "password", "name", "email")
            .contains(encryptedLoginId, encryptedNewPassword, encryptedNewName, encryptedNewEmail)
    }

}