package com.interrupt.server.member.dto.recover

data class VerifyRecoverPasswordRequest(
    val memberRecoverKey: String,
    val verifyCode: String,
    val password: String,
) {

}
