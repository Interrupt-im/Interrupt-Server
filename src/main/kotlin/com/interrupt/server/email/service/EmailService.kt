package com.interrupt.server.email.service

import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.email.dto.EmailSendDto
import com.interrupt.server.email.dto.EmailType
import com.interrupt.server.email.dto.verify.EmailAddressVerificationApplyRequest
import com.interrupt.server.email.dto.verify.EmailAddressVerificationApplyResponse
import com.interrupt.server.email.dto.verify.EmailAddressVerifyRequest
import com.interrupt.server.email.dto.verify.EmailAddressVerifyResponse
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val emailSendService: EmailSendService,
    private val stringEncoder: StringEncoder,
) {

    fun applyVerificationEmailAddress(emailAddressVerificationApplyRequest: EmailAddressVerificationApplyRequest): EmailAddressVerificationApplyResponse {
        val sendResult = emailSendService.sendMail(EmailSendDto(EmailType.MEMBER_REGISTER, emailAddressVerificationApplyRequest.emailAddress, EmailType.MEMBER_REGISTER.subject))

        return EmailAddressVerificationApplyResponse(sendResult)
    }

    fun verifyEmailAddress(emailAddressVerifyRequest: EmailAddressVerifyRequest): EmailAddressVerifyResponse {
        // TODO 레디스에서 인증 코드를 검증하는 로직(email 주소도 함께 확인)
        // TODO 레디스에 이메일을 암호화 하여 저장(유효시간 설정, 해당 유효시간이 메일 인증 후 회원가입 할 수 있는 유효시간)

        return EmailAddressVerifyResponse(true)
    }

}