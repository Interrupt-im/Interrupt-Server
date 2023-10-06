package com.interrupt.server.email.dto

enum class EmailType(
    val template: String,
    val subject: String,
) {

    MEMBER_REGISTER("email/member-register", "[INTERRUPT] 회원 가입 인증 메일 입니다."),

}