package com.interrupt.server.email.dto

import com.interrupt.server.email.service.EmailType

data class EmailSendDto(
    val emailType: EmailType,
    val receiver: String,
    val subject: String? = null,
) {
}