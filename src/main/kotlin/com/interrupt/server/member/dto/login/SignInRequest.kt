package com.interrupt.server.member.dto.login

import com.interrupt.server.member.validation.annotation.loginid.LoginIdValidation
import com.interrupt.server.member.validation.annotation.password.PasswordValidation
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignInRequest(
    @LoginIdValidation
    @field:NotBlank(message = "아이디 값은 필수 입니다.")
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]+\$", message = "아이디는 영어(필수)와 숫자로 설정해야 합니다.")
    @field:Size(min = 8, max = 20, message = "아이디는 8자 이상 20자 이하로 설정해야 합니다.")
    var loginId: String?,
    @PasswordValidation
    @field:NotBlank(message = "비밀번호 값은 필수 입니다.")
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\$%^&*()-_+=<>?]+\$", message = "비밀번호는 영어(필수), 숫자(필수), 특수문자(선택)로 설정해야 합니다.")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 설정해야 합니다.")
    var password: String?,
) {

}
