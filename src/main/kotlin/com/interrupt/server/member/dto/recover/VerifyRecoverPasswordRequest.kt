package com.interrupt.server.member.dto.recover

import com.interrupt.server.member.validation.annotation.email.EmailVerifyCodeValidation
import com.interrupt.server.member.validation.annotation.password.PasswordValidation
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class VerifyRecoverPasswordRequest(
    @field:NotBlank(message = "관리자에게 문의 바랍니다.")
    val memberRecoverKey: String?,
    @EmailVerifyCodeValidation
    @field:NotBlank(message = "이메일 인증코드 값은 필수 입니다.")
    @field:Pattern(regexp = "^[0-9]+\$", message = "이메일 인증코드는 숫자만으로 입력하셔야 합니다.")
    @field:Size(min = 6, max = 6, message = "이메일 인증코드는 6자리 입니다.")
    val verifyCode: String?,
    @PasswordValidation
    @field:NotBlank(message = "비밀번호 값은 필수 입니다.")
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\$%^&*()-_+=<>?]+\$", message = "비밀번호는 영어(필수), 숫자(필수), 특수문자(선택)로 설정해야 합니다.")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 설정해야 합니다.")
    val password: String?,
) {

}
