package com.interrupt.server.email.dto

enum class EmailType(
    val template: String,
    val subject: String,
) {

    MEMBER_REGISTER("email/member-register", "[INTERRUPT] 회원 가입 인증 메일 입니다."),
    LOGIN_ID_RECOVER("email/member-recover", "[INTERRUPT] ID 찾기 인증 메일 입니다."),
    PASSWORD_RECOVER("email/member-recover", "[INTERRUPT] 비밀번호 찾기 인증 메일 입니다."),

}