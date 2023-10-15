package com.interrupt.server.member.dto.recover

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class RecoverPasswordRequest(
    @field:NotBlank(message = "아이디를 입력하지 않았습니다.")
    @field:Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "8자 이상 20자 이하의 영어, 숫자 값을 입력해주세요.")
    val loginId: String?,
    @field:NotBlank(message = "이메일을 입력하지 않았습니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String?
) {

}
