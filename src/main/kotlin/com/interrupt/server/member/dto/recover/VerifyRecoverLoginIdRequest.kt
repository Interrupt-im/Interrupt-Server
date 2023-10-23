package com.interrupt.server.member.dto.recover

import jakarta.validation.constraints.NotBlank

data class VerifyRecoverLoginIdRequest(
    @field:NotBlank(message = "관리자에게 문의 바랍니다.")
    val memberRecoverKey: String?,
    @field:NotBlank(message = "이메일 인증코드를 입력하지 않았습니다.")
    val verifyCode: String?,
) {

}
