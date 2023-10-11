package com.interrupt.server.member.service

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.email.dto.EmailContent
import com.interrupt.server.email.dto.EmailType
import com.interrupt.server.email.dto.EmailType.LOGIN_ID_RECOVER
import com.interrupt.server.email.dto.EmailType.PASSWORD_RECOVER
import com.interrupt.server.email.entity.EmailMessage
import com.interrupt.server.member.entity.EmailVerifyCode
import com.interrupt.server.member.repository.EmailVerifyCodeRepository
import com.interrupt.server.email.service.EmailSendService
import com.interrupt.server.member.dto.delete.MemberDeleteRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckResponse
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyRequest
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyResponse
import com.interrupt.server.member.dto.emailverify.EmailVerifyRequest
import com.interrupt.server.member.dto.emailverify.EmailVerifyResponse
import com.interrupt.server.member.dto.login.MemberLoginRequest
import com.interrupt.server.member.dto.login.MemberLoginResponse
import com.interrupt.server.member.dto.recover.*
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.dto.update.MemberUpdateRequest
import com.interrupt.server.member.dto.update.MemberUpdateResponse
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.entity.MemberRecover
import com.interrupt.server.member.repository.MemberRecoverRepository
import com.interrupt.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.random.Random

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val emailSendService: EmailSendService,
    private val emailVerifyCodeRepository: EmailVerifyCodeRepository,
    private val memberRecoverRepository: MemberRecoverRepository,
    private val stringEncoder: StringEncoder,
) {

    @Value("\${security.encrypt-key}")
    private lateinit var secretKey: String

    /**
     * 회원 가입
     */
    @Transactional
    fun registerMember(memberRegisterRequest: MemberRegisterRequest) {
        val encryptedEmail = stringEncoder.encrypt(memberRegisterRequest.email, secretKey)
        val emailVerifyCode = emailVerifyCodeRepository.findByUuid(encryptedEmail)
        validateVerifiedEmail(emailVerifyCode)

        val encryptedLoginId = stringEncoder.encrypt(memberRegisterRequest.loginId, secretKey)
        validateDuplicatedLoginId(memberRepository.findByLoginId(encryptedLoginId))

        val member = memberRegisterRequest.also {
            it.loginId = encryptedLoginId
            it.password = stringEncoder.encrypt(it.password, secretKey)
            it.name = stringEncoder.encrypt(it.name, secretKey)
            it.email = encryptedEmail
        }.toEntity()

        memberRepository.save(member)
    }

    /**
     * 회원 ID 중복 체크
     */
    fun checkLoginIdDuplication(request: LoginIdDuplicateCheckRequest): LoginIdDuplicateCheckResponse =
        memberRepository.findByLoginId(stringEncoder.encrypt(request.loginId, secretKey))
            ?.let { LoginIdDuplicateCheckResponse(isUnique = false) }
            ?: LoginIdDuplicateCheckResponse(isUnique = true)

    /**
     * 이메일 인증 코드 발송
     */
    fun applySendEmailVerifyCode(emailVerificationApplyRequest: EmailVerificationApplyRequest): EmailVerificationApplyResponse {
        val verifyCode = generateRandomCode()

        val content = emailSendService.generateEmailTemplate(EmailType.MEMBER_REGISTER.template, mapOf(("code" to verifyCode)))

        val emailMessage = EmailMessage(
            to = emailVerificationApplyRequest.email,
            emailContents = EmailContent(EmailType.MEMBER_REGISTER.subject, content)
        )

        emailSendService.sendMail(emailMessage)

        val encryptedEmail = stringEncoder.encrypt(emailVerificationApplyRequest.email, secretKey)

        val emailVerifyCode = emailVerifyCodeRepository.save(EmailVerifyCode(encryptedEmail, verifyCode))

        return EmailVerificationApplyResponse(emailVerifyCode.uuid)
    }

    /**
     * 이메일 인증 코드 확인
     */
    fun validateEmailVerifyCode(emailVerifyRequest: EmailVerifyRequest): EmailVerifyResponse {
        emailVerifyCodeRepository.findByUuid(emailVerifyRequest.emailVerifyCodeKey)?.let {
            validateVerifyCode(emailVerifyRequest.verifyCode, it.verifyCode)

            it.isVerified = true
            emailVerifyCodeRepository.save(it)

            return EmailVerifyResponse(true)
        } ?: throw InterruptServerException(errorCode = ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND)
    }

    /**
     * 로그인
     */
    fun login(memberLoginRequest: MemberLoginRequest): MemberLoginResponse {
        val encryptedLoginId = stringEncoder.encrypt(memberLoginRequest.loginId, secretKey)
        val encryptedPassword = stringEncoder.encrypt(memberLoginRequest.password, secretKey)

        memberRepository.findByLoginIdAndPassword(encryptedLoginId, encryptedPassword)?.let {
            return MemberLoginResponse.of(it).also { response ->
                response.name = stringEncoder.decrypt(response.name, secretKey)
            }

        } ?: throw InterruptServerException(errorCode = ErrorCode.FAILED_LOGIN)
    }

    /**
     * 회원 탈퇴
     */
    fun deleteMember(memberDeleteRequest: MemberDeleteRequest) {
        val encryptedLoginId = stringEncoder.encrypt(memberDeleteRequest.loginId, secretKey)
        val encryptedPassword = stringEncoder.encrypt(memberDeleteRequest.password, secretKey)

        val foundMember = memberRepository.findByLoginIdAndPassword(encryptedLoginId, encryptedPassword)
        validateMember(foundMember)
        foundMember!!.deletedAt = LocalDateTime.now()
        memberRepository.save(foundMember)
    }

    /**
     * 회원 ID 찾기 메일 인증 요청
     */
    fun applySendLoginIdRecoverVerifyCode(recoverLoginIdRequest: RecoverLoginIdRequest): RecoverLoginIdResponse {

        val encryptedName = stringEncoder.encrypt(recoverLoginIdRequest.name, secretKey)
        val encryptedEmail = stringEncoder.encrypt(recoverLoginIdRequest.email, secretKey)
        val foundMember = memberRepository.findByNameAndEmail(encryptedName, encryptedEmail)

        validateMember(foundMember)

        val verifyCode = generateRandomCode()
        val content = emailSendService.generateEmailTemplate(LOGIN_ID_RECOVER.template, mapOf(("notice" to "아이디"), ("code" to verifyCode)))

        val emailMessage = EmailMessage(
            to = recoverLoginIdRequest.email,
            emailContents = EmailContent(content, LOGIN_ID_RECOVER.subject)
        )

        emailSendService.sendMail(emailMessage)

        val memberRecover = memberRecoverRepository.save(MemberRecover(encryptedEmail, foundMember!!.loginId, verifyCode))

        return RecoverLoginIdResponse(memberRecover.uuid)
    }

    /**
     * 회원 ID 찾기 메일 인증 확인
     */
    fun validateLoginIdRecoverVerifyCode(recoverLoginIdRequest: VerifyRecoverLoginIdRequest): VerifyRecoverLoginIdResponse {
        memberRecoverRepository.findByUuid(recoverLoginIdRequest.memberRecoverKey)?.let {
            validateVerifyCode(enteredVerifyCode = recoverLoginIdRequest.verifyCode, it.verifyCode)

            return VerifyRecoverLoginIdResponse(stringEncoder.decrypt(it.loginId, secretKey))
        } ?: throw InterruptServerException(errorCode = ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND)
    }

    /**
     * 비밀번호 찾기 메일 인증 요청
     */
    fun applySendPasswordRecoverVerifyCode(recoverPasswordRequest: RecoverPasswordRequest): RecoverPasswordResponse {

        val encryptedLoginId = stringEncoder.encrypt(recoverPasswordRequest.loginId, secretKey)
        val encryptedEmail = stringEncoder.encrypt(recoverPasswordRequest.email, secretKey)

        val verifyCode = generateRandomCode()
        val content = emailSendService.generateEmailTemplate(PASSWORD_RECOVER.template, mapOf(("recover" to "비밀번호"), ("code" to verifyCode)))

        val emailMessage = EmailMessage(
            to = recoverPasswordRequest.email,
            emailContents = EmailContent(content, PASSWORD_RECOVER.subject)
        )

        emailSendService.sendMail(emailMessage)

        val memberRecover = memberRecoverRepository.save(MemberRecover(encryptedEmail, encryptedLoginId, verifyCode))

        return RecoverPasswordResponse(memberRecover.uuid)
    }

    /**
     * 비밀번호 찾기 메일 인증 확인
     */
    fun validatePasswordRecoverVerifyCode(recoverPasswordRequest: VerifyRecoverPasswordRequest): VerifyRecoverPasswordResponse {
        memberRecoverRepository.findByUuid(recoverPasswordRequest.memberRecoverKey)?.let {
            validateVerifyCode(enteredVerifyCode = recoverPasswordRequest.verifyCode, it.verifyCode)

            val foundMember = memberRepository.findByLoginId(it.loginId)
            validateMember(foundMember)

            foundMember!!.password = recoverPasswordRequest.password
            memberRepository.save(foundMember)

            return VerifyRecoverPasswordResponse(true)
        } ?: throw InterruptServerException(errorCode = ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND)
    }

    /**
     * 회원 정보 수정
     */
    fun updateMember(memberUpdateRequest: MemberUpdateRequest): MemberUpdateResponse {
        val originalMember = memberRepository.findByLoginId(memberUpdateRequest.originalLoginId)
        validateMember(originalMember)

        memberUpdateRequest.loginId?.let {
            val encryptedLoginId = stringEncoder.encrypt(it, secretKey)
            validateDuplicatedLoginId(memberRepository.findByLoginId(encryptedLoginId))

            originalMember!!.loginId = encryptedLoginId
        }

        memberUpdateRequest.password?.let {
            originalMember!!.password = stringEncoder.encrypt(it, secretKey)
        }

        memberUpdateRequest.name?.let {
            originalMember!!.name = stringEncoder.encrypt(it, secretKey)
        }

        memberUpdateRequest.email?.let {
            val encryptedEmail = stringEncoder.encrypt(it, secretKey)
            val emailVerifyCode = emailVerifyCodeRepository.findByUuid(encryptedEmail)
            validateVerifiedEmail(emailVerifyCode)

            originalMember!!.email = encryptedEmail
        }

        memberRepository.save(originalMember!!)

        return MemberUpdateResponse(true)
    }

    private fun validateMember(member: Member?) {
        if (member == null) throw InterruptServerException(errorCode = ErrorCode.MEMBER_NOT_FOUND)
    }

    private fun validateVerifyCode(enteredVerifyCode: String, verifyCode: String) {
        if (enteredVerifyCode != verifyCode) throw InterruptServerException(errorCode = ErrorCode.INVALID_EMAIL_VERIFY_CODE)
    }

    private fun validateDuplicatedLoginId(foundMember: Member?) {
        if (foundMember != null) throw InterruptServerException(errorCode = ErrorCode.DUPLICATED_REGISTER_LOGIN_ID)
    }

    private fun validateVerifiedEmail(emailVerifyCode: EmailVerifyCode?) {
        if (emailVerifyCode == null || emailVerifyCode.isVerified.not()) throw InterruptServerException(errorCode = ErrorCode.EMAIL_NOT_VERIFIED)
    }

    private fun generateRandomCode(): String {
        val min = 0
        val max = 999_999
        val randomNumber = Random.nextInt(min, max + 1)
        return String.format("%06d", randomNumber)
    }

}
