package com.interrupt.server.email.dto

data class EmailSendDto(
    val emailType: EmailType,
    val receiver: String,
    val subject: String? = null,
) {
}