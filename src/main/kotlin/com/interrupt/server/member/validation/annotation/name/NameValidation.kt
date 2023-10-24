package com.interrupt.server.member.validation.annotation.name

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "이름 값은 필수 입니다.")
annotation class NameValidation