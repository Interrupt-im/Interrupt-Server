package com.interrupt.server.member.validation.annotation.email

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "이메일 값은 필수 입니다.")
@Email(message = "올바른 이메일 형식으로 입력하셔야 합니다.")
annotation class EmailValidation