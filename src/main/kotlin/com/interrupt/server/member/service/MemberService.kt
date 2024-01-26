package com.interrupt.server.member.service

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.email.dto.EmailContent
import com.interrupt.server.email.dto.EmailType
import com.interrupt.server.email.dto.EmailType.LOGIN_ID_RECOVER
import com.interrupt.server.email.dto.EmailType.PASSWORD_RECOVER
import com.interrupt.server.email.entity.EmailMessage
import com.interrupt.server.email.service.EmailSendService
import com.interrupt.server.member.dto.delete.MemberDeleteRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckResponse
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyRequest
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyResponse
import com.interrupt.server.member.dto.emailverify.EmailVerifyRequest
import com.interrupt.server.member.dto.recover.*
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.dto.update.MemberUpdateRequest
import com.interrupt.server.member.entity.EmailVerifyCode
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.entity.MemberRecover
import com.interrupt.server.member.repository.EmailVerifyCodeRepository
import com.interrupt.server.member.repository.MemberRecoverRepository
import com.interrupt.server.member.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
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
    private val passwordEncoder: PasswordEncoder,
) {

    /**
     * 회원 가입
     */
    @Transactional
    fun registerMember(memberRegisterRequest: MemberRegisterRequest) {
        val emailVerifyCode = emailVerifyCodeRepository.findByUuid(memberRegisterRequest.emailVerifyCodeKey!!)
        validateVerifiedEmail(emailVerifyCode)

        validateDuplicatedLoginId(memberRepository.findByLoginId(memberRegisterRequest.loginId!!))

        val member = memberRegisterRequest.let {
            Member(it.loginId!!, passwordEncoder.encode(it.password), it.name!!, it.email!!)
        }

        memberRepository.save(member)
    }

    /**
     * 회원 ID 중복 체크
     */
    fun checkLoginIdDuplication(request: LoginIdDuplicateCheckRequest): LoginIdDuplicateCheckResponse =
        memberRepository.findByLoginId(request.loginId!!)
            ?.let { LoginIdDuplicateCheckResponse(isUnique = false) }
            ?: LoginIdDuplicateCheckResponse(isUnique = true)

    /**
     * 이메일 인증 코드 발송
     */
    fun applySendEmailVerifyCode(emailVerificationApplyRequest: EmailVerificationApplyRequest): EmailVerificationApplyResponse {
        val verifyCode = generateRandomCode()

        val content = emailSendService.generateEmailTemplate(EmailType.MEMBER_REGISTER.template, mapOf(("code" to verifyCode)))

        val emailMessage = EmailMessage(
            to = emailVerificationApplyRequest.email!!,
            emailContents = EmailContent(EmailType.MEMBER_REGISTER.subject, content)
        )

        emailSendService.sendMail(emailMessage)

        val emailVerifyCode = emailVerifyCodeRepository.save(EmailVerifyCode(emailVerificationApplyRequest.email, verifyCode))

        return EmailVerificationApplyResponse(emailVerifyCode.uuid)
    }

    /**
     * 이메일 인증 코드 확인
     */
    fun validateEmailVerifyCode(emailVerifyRequest: EmailVerifyRequest) {
        val foundVerifyCode = emailVerifyCodeRepository.findByUuid(emailVerifyRequest.emailVerifyCodeKey!!)
        validateExistEmailVerifyCode(foundVerifyCode)

        validateCorrectVerifyCode(emailVerifyRequest.verifyCode!!, foundVerifyCode!!.verifyCode)

        foundVerifyCode.isVerified = true
        emailVerifyCodeRepository.save(foundVerifyCode)
    }

    /**
     * 회원 탈퇴
     */
    fun deleteMember(member: Member, memberDeleteRequest: MemberDeleteRequest) {
        val foundMember = memberRepository.findByLoginId(member.loginId)
        validateExistMember(foundMember)
        validateUserCredentials(foundMember!!, memberDeleteRequest.password!!)

        foundMember.deletedAt = LocalDateTime.now()
        memberRepository.save(foundMember)
    }

    /**
     * 회원 ID 찾기 메일 인증 요청
     */
    fun applySendLoginIdRecoverVerifyCode(recoverLoginIdRequest: RecoverLoginIdRequest): RecoverLoginIdResponse {

        val foundMember = recoverLoginIdRequest.let {
            memberRepository.findByNameAndEmail(it.name!!, it.email!!)
        }

        validateExistMember(foundMember)

        val verifyCode = generateRandomCode()
        val content = emailSendService.generateEmailTemplate(LOGIN_ID_RECOVER.template, mapOf(("notice" to "아이디"), ("code" to verifyCode)))

        val emailMessage = EmailMessage(
            to = recoverLoginIdRequest.email!!,
            emailContents = EmailContent(LOGIN_ID_RECOVER.subject, content)
        )

        emailSendService.sendMail(emailMessage)

        val memberRecover = memberRecoverRepository.save(MemberRecover(recoverLoginIdRequest.email, foundMember!!.loginId, verifyCode))

        return RecoverLoginIdResponse(memberRecover.uuid)
    }

    /**
     * 회원 ID 찾기 메일 인증 확인
     */
    fun validateLoginIdRecoverVerifyCode(recoverLoginIdRequest: VerifyRecoverLoginIdRequest): VerifyRecoverLoginIdResponse {
        val foundMemberRecover = memberRecoverRepository.findByUuid(recoverLoginIdRequest.memberRecoverKey!!)
        validateExistMemberRecover(foundMemberRecover)

        validateCorrectVerifyCode(enteredVerifyCode = recoverLoginIdRequest.verifyCode!!, foundMemberRecover!!.verifyCode)

        return VerifyRecoverLoginIdResponse(foundMemberRecover.loginId)
    }

    /**
     * 비밀번호 찾기 메일 인증 요청
     */
    fun applySendPasswordRecoverVerifyCode(recoverPasswordRequest: RecoverPasswordRequest): RecoverPasswordResponse {

        val foundMember = recoverPasswordRequest.let {
            memberRepository.findByLoginIdAndEmail(it.loginId!!, it.email!!)
        }
        validateExistMember(foundMember)

        val verifyCode = generateRandomCode()
        val content = emailSendService.generateEmailTemplate(PASSWORD_RECOVER.template, mapOf(("recover" to "비밀번호"), ("code" to verifyCode)))

        val emailMessage = EmailMessage(
            to = recoverPasswordRequest.email!!,
            emailContents = EmailContent(content, PASSWORD_RECOVER.subject)
        )

        emailSendService.sendMail(emailMessage)

        val memberRecover = memberRecoverRepository.save(MemberRecover(foundMember!!.email, foundMember.loginId, verifyCode))

        return RecoverPasswordResponse(memberRecover.uuid)
    }

    /**
     * 비밀번호 찾기 메일 인증 확인
     */
    fun validatePasswordRecoverVerifyCode(recoverPasswordRequest: VerifyRecoverPasswordRequest) {
        val foundMemberRecover = memberRecoverRepository.findByUuid(recoverPasswordRequest.memberRecoverKey!!)
        validateExistMemberRecover(foundMemberRecover)
        validateCorrectVerifyCode(enteredVerifyCode = recoverPasswordRequest.verifyCode!!, foundMemberRecover!!.verifyCode)

        val foundMember = memberRepository.findByLoginId(foundMemberRecover.loginId)
        validateExistMember(foundMember)

        foundMember!!.loginPassword = passwordEncoder.encode(recoverPasswordRequest.password!!)
        memberRepository.save(foundMember)
    }

    /**
     * 회원 정보 수정
     */
    fun updateMember(member: Member, memberUpdateRequest: MemberUpdateRequest) {
        memberUpdateRequest.password?.let {
            memberUpdateRequest.password = passwordEncoder.encode(it)
        }

        memberUpdateRequest.email?.let {
            val emailVerifyCode = memberUpdateRequest.emailVerifyCodeKey?.let { emailVerifyCodeKey->
                emailVerifyCodeRepository.findByUuid(emailVerifyCodeKey)
            }
            validateVerifiedEmail(emailVerifyCode)
        }

        member.update(memberUpdateRequest)

        memberRepository.save(member)
    }

    private fun validateExistMember(member: Member?) {
        if (member == null) throw InterruptServerException(errorCode = ErrorCode.MEMBER_NOT_FOUND)
    }

    private fun validateUserCredentials(member: Member, password: String) {
        if (!passwordEncoder.matches(password, member.loginPassword)) throw InterruptServerException(errorCode = ErrorCode.FAILED_LOGIN)
    }

    private fun validateCorrectVerifyCode(enteredVerifyCode: String, verifyCode: String) {
        if (enteredVerifyCode != verifyCode) throw InterruptServerException(errorCode = ErrorCode.INVALID_EMAIL_VERIFY_CODE)
    }

    private fun validateExistEmailVerifyCode(emailVerifyCode: EmailVerifyCode?) {
        if (emailVerifyCode == null) throw InterruptServerException(errorCode = ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND)
    }

    private fun validateDuplicatedLoginId(foundMember: Member?) {
        if (foundMember != null) throw InterruptServerException(errorCode = ErrorCode.DUPLICATED_REGISTER_LOGIN_ID)
    }

    private fun validateVerifiedEmail(emailVerifyCode: EmailVerifyCode?) {
        validateExistEmailVerifyCode(emailVerifyCode)
        if (emailVerifyCode!!.isVerified.not()) throw InterruptServerException(errorCode = ErrorCode.EMAIL_NOT_VERIFIED)
    }

    private fun validateExistMemberRecover(emailVerifyCode: MemberRecover?) {
        if (emailVerifyCode == null) throw InterruptServerException(errorCode = ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND)
    }

    private fun generateRandomCode(): String {
        val min = 0
        val max = 999_999
        val randomNumber = Random.nextInt(min, max + 1)
        return String.format("%06d", randomNumber)
    }

}
