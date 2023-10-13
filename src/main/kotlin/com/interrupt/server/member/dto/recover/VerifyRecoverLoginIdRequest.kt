package com.interrupt.server.member.dto.recover

data class VerifyRecoverLoginIdRequest(
    val memberRecoverKey: String,
    val verifyCode: String,
) {

}
