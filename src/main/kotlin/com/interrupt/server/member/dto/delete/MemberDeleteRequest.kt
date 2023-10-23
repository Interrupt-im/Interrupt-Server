package com.interrupt.server.member.dto.delete

import jakarta.validation.constraints.Pattern

data class MemberDeleteRequest(
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\$%^&*()-_+=<>?]{8,20}\$", message = "비밀번호는 8자 이상 20자 이하의 영어, 숫자, 특수문자 값을 입력해주세요.")
    var password: String?
) {
}
