package com.interrupt.server.member.validation.annotation.email

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Email(message = "올바른 이메일 형식이 아닙니다.")
annotation class NullableEmailValidation