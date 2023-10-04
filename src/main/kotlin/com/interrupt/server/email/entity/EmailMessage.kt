package com.interrupt.server.email.entity

import com.interrupt.server.email.dto.content.EmailContent
import com.interrupt.server.email.service.EmailType

data class EmailMessage(
    val to: String,
    val emailType: EmailType,
    val emailContents: EmailContent
) {
}