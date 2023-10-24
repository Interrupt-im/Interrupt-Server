package com.interrupt.server.member.validation.annotation.email

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "이메일 인증코드 값은 필수 입니다.")
@Pattern(regexp = "^[0-9]+\$", message = "이메일 인증코드는 숫자만으로 입력하셔야 합니다.")
@Size(min = 6, max = 6, message = "이메일 인증코드는 6자리 입니다.")
annotation class EmailVerifyCodeValidation