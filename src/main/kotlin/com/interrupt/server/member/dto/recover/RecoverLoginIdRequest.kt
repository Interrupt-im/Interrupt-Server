package com.interrupt.server.member.dto.recover

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RecoverLoginIdRequest(
    @field:NotBlank(message = "이름을 입력하지 않았습니다.")
    val name: String?,
    @field:NotBlank(message = "이메일을 입력하지 않았습니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String?
) {

}
