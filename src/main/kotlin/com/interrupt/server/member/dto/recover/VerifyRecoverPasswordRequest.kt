package com.interrupt.server.member.dto.recover

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class VerifyRecoverPasswordRequest(
    @field:NotBlank(message = "관리자에게 문의 바랍니다.")
    val memberRecoverKey: String?,
    @field:NotBlank(message = "이메일 인증코드를 입력하지 않았습니다.")
    val verifyCode: String?,
    @field:NotBlank(message = "비밀번호를 입력하지 않았습니다.")
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\$%^&*()-_+=<>?]{8,20}\$", message = "비밀번호는 8자 이상 20자 이하의 영어, 숫자, 특수문자 값을 입력해주세요.")
    val newPassword: String?,
) {

}
