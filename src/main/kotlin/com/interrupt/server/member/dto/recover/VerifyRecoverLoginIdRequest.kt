package com.interrupt.server.member.dto.recover

import com.interrupt.server.member.validation.annotation.email.EmailVerifyCodeValidation
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class VerifyRecoverLoginIdRequest(
    @field:NotBlank(message = "관리자에게 문의 바랍니다.")
    val memberRecoverKey: String?,
    @EmailVerifyCodeValidation
    @field:NotBlank(message = "이메일 인증코드 값은 필수 입니다.")
    @field:Pattern(regexp = "^[0-9]+\$", message = "이메일 인증코드는 숫자만으로 입력하셔야 합니다.")
    @field:Size(min = 6, max = 6, message = "이메일 인증코드는 6자리 입니다.")
    val verifyCode: String?,
) {

}
