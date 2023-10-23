package com.interrupt.server.member.dto.duplicatedidcheck

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class LoginIdDuplicateCheckRequest(
    @field:NotBlank(message = "아이디를 입력하지 않았습니다.")
    @field:Pattern(regexp = "(?=.*[a-zA-Z])[a-zA-Z0-9]{8,20}\$", message = "8자 이상 20자 이하의 영어, 숫자 값을 입력해주세요.")
    val loginId: String?
) {

}
