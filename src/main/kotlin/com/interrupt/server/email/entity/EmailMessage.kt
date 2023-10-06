package com.interrupt.server.email.entity

import com.interrupt.server.email.dto.EmailContent
import com.interrupt.server.email.dto.EmailType

data class EmailMessage(
    val to: String,
    val emailType: EmailType,
    val emailContents: EmailContent
) {
}