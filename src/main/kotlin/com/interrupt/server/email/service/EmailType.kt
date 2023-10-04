package com.interrupt.server.email.service

enum class EmailType(
    val template: String,
    val subject: String,
    val variables: Map<String, Any>
) {

    MEMBER_REGISTER("email/member-register", "[INTERRUPT] 회원 가입 인증 메일 입니다.", mapOf(("code" to ""))),

}