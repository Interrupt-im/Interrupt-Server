package com.interrupt.server.member.dto.login

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class MemberLoginRequest(
    @field:NotBlank(message = "아이디를 입력하지 않았습니다.")
    @field:Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "8자 이상 20자 이하의 영어, 숫자 값을 입력해주세요.")
    var loginId: String?,
    @field:NotBlank(message = "비밀번호를 입력하지 않았습니다.")
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\$%^&*()-_+=<>?]{8,20}\$", message = "비밀번호는 8자 이상 20자 이하의 영어, 숫자 값을 입력해주세요.")
    var password: String?,
) {

}
