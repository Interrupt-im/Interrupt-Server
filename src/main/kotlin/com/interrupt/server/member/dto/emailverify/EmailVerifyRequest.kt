package com.interrupt.server.member.dto.emailverify

data class EmailVerifyRequest(
    val emailVerifyCodeKey: String,
    val verifyCode: String,
) {

}
