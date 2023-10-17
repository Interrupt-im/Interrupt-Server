package com.interrupt.server.member.dto.update

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class MemberUpdateRequest(
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\$%^&*()-_+=<>?]{8,20}\$", message = "비밀번호는 8자 이상 20자 이하의 영어, 숫자, 특수문자 값을 입력해주세요.")
    var password: String? = null,
    var name: String? = null,
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    var email: String? = null,
    val emailVerifyCodeKey: String?,
) {
    lateinit var loginId: String
}
