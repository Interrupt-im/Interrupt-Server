package com.interrupt.server.member.dto.update

import com.interrupt.server.member.validation.annotation.email.NullableEmailValidation
import com.interrupt.server.member.validation.annotation.password.NullablePasswordValidation
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class MemberUpdateRequest(
    @NullablePasswordValidation
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\$%^&*()-_+=<>?]+\$", message = "비밀번호는 영어(필수), 숫자(필수), 특수문자(선택)로 설정해야 합니다.")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 설정해야 합니다.")
    var password: String? = null,
    var name: String? = null,
    @NullableEmailValidation
    @field:Email(message = "올바른 이메일 형식으로 입력하셔야 합니다.")
    var email: String? = null,
    val emailVerifyCodeKey: String? = null,
) {
}
