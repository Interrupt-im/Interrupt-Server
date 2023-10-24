package com.interrupt.server.member.validation.annotation.loginid

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@MustBeDocumented
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "아이디 값은 필수 입니다.")
@Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]+\$", message = "아이디는 영어(필수)와 숫자로 설정해야 합니다.")
@Size(min = 8, max = 20, message = "아이디는 8자 이상 20자 이하로 설정해야 합니다.")
annotation class LoginIdValidation