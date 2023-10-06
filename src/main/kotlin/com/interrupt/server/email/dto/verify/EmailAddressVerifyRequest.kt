package com.interrupt.server.email.dto.verify

data class EmailAddressVerifyRequest(
    val emailAddress: String,
    val verifyCode: String,
) {

}
