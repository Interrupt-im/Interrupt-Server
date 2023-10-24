package com.interrupt.server.member.dto.recover

import com.interrupt.server.member.validation.annotation.email.EmailValidation
import com.interrupt.server.member.validation.annotation.name.NameValidation
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RecoverLoginIdRequest(
    @NameValidation
    @field:NotBlank(message = "이름 값은 필수 입니다.")
    val name: String?,
    @EmailValidation
    @field:NotBlank(message = "이메일 값은 필수 입니다.")
    @field:Email(message = "올바른 이메일 형식으로 입력하셔야 합니다.")
    val email: String?
) {

}
