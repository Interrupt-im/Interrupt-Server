package com.interrupt.server.member.dto.recover

data class RecoverPasswordRequest(
    val loginId: String,
    val email: String
) {

}
