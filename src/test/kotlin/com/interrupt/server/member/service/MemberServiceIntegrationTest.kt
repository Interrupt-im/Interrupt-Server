package com.interrupt.server.member.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.email.dto.EmailContent
import com.interrupt.server.email.dto.EmailType
import com.interrupt.server.email.entity.EmailMessage
import com.interrupt.server.email.service.EmailSendService
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyRequest
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.entity.EmailVerifyCode
import com.interrupt.server.member.repository.EmailVerifyCodeRepository
import com.interrupt.server.member.repository.MemberRecoverRepository
import com.interrupt.server.member.repository.MemberRepository
import com.ninjasquad.springmockk.SpykBean
import io.mockk.justRun
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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

    @Value("\${security.encrypt-key}")
    private lateinit var secretKey: String

    @Test
    fun `회원 가입 dto 를 통해 받은 정보로 회원 가입을 수행 한다`() {
        // given
        val email = "test@mail.com"
        val emailVerifyCodeKey = "0000"
        val encryptedEmail = stringEncoder.encrypt(email, secretKey)

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
        val email = "djawnstj44@naver.com"
        val request = EmailVerificationApplyRequest(email)

        justRun { emailSendService.sendMail(EmailMessage(email, EmailContent(EmailType.MEMBER_REGISTER.subject, "testContent"))) }

        // when
        val response = memberService.applySendEmailVerifyCode(request)

        // then
        assertThat(response.emailVerifyCodeKey).isNotNull()
    }

}