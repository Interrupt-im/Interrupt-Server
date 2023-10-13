package com.interrupt.server.member.dto.duplicatedidcheck

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginIdDuplicateCheckRequest(
    @field:NotNull(message = "아이디 입력은 필수입니다.")
    @field:NotBlank(message = "아이디 입력은 필수입니다.")
    @field:Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영어와 숫자만 허용됩니다.")
    @field:Size(min = 8, max = 20, message = "아이디를 8자에서 20자 사이로 입력해주세요.")
    val loginId: String
) {

}
