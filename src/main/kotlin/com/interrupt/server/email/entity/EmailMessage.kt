package com.interrupt.server.email.entity

import com.interrupt.server.email.dto.EmailContent
import com.interrupt.server.email.dto.EmailType

class EmailMessage(
    val to: String,
    val emailContents: EmailContent
) {

    override fun toString(): String {
        return "EmailMessage(to='$to', emailContents=${emailContents.subject})"
    }
}