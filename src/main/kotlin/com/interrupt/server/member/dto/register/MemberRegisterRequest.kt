package com.interrupt.server.member.dto.register

import com.interrupt.server.member.entity.Member
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class MemberRegisterRequest(
    @field:NotBlank(message = "아이디를 입력하지 않았습니다.")
    @field:Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "8자 이상 20자 이하의 영어, 숫자 값을 입력해주세요.")
    var loginId: String?,
    @field:NotBlank(message = "비밀번호를 입력하지 않았습니다.")
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9!@#\$%^&*()-_+=<>?]{8,20}\$", message = "비밀번호는 8자 이상 20자 이하의 영어, 숫자 값을 입력해주세요.")
    var password: String?,
    @field:NotBlank(message = "이름을 입력하지 않았습니다.")
    var name: String?,
    @field:NotBlank(message = "이메일을 입력하지 않았습니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    var email: String?,
    @field:NotBlank(message = "관리자에게 문의 바랍니다.")
    val emailVerifyCodeKey: String?,
) {

    fun toEntity(): Member =
        Member(loginId = loginId!!, password = password!!, name = name!!, email = email!!,)

}