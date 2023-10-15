package com.interrupt.server.member.dto.emailverify

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailVerifyRequest(
    @field:NotBlank(message = "이메일을 입력하지 않았습니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String?,
    @field:NotBlank(message = "관리자에게 문의 바랍니다.")
    val emailVerifyCodeKey: String?,
    @field:NotBlank(message = "이메일 인증코드를 입력하지 않았습니다.")
    val verifyCode: String?,
) {

}
