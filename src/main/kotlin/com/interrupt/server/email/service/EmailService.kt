package com.interrupt.server.email.service

import com.interrupt.server.email.dto.EmailSendDto
import com.interrupt.server.email.dto.content.EmailContent
import com.interrupt.server.email.entity.EmailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import kotlin.random.Random

@Service
class EmailService(
    private val javaMailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
) {

    fun sendMail(emailSendDto: EmailSendDto) {
        val mimeMessage = javaMailSender.createMimeMessage()

        val emailContent: EmailContent = createEmailContent(emailSendDto)

        val emailMessage = EmailMessage(emailSendDto.receiver, emailSendDto.emailType, emailContent)

        MimeMessageHelper(mimeMessage, false, "UTF-8").apply {
            setTo(emailMessage.to)
            setSubject(emailMessage.emailContents.subject)
            setText(emailMessage.emailContents.content, true)
        }

        javaMailSender.send(mimeMessage)
    }

    private fun createEmailContent(emailSendDto: EmailSendDto): EmailContent {
        val type = emailSendDto.emailType
        val content = when(type) {
            EmailType.MEMBER_REGISTER -> {
                val code = generateRandomCode()
//                type.variables.get("code") = code
                templateEngine.process(
                    type.template,
                    Context().apply { setVariable("code", code) }
                )
            }
        }

        val subject: String =
            if (!emailSendDto.subject.isNullOrBlank()) emailSendDto.subject
            else emailSendDto.emailType.subject

        return EmailContent(subject, content)
    }

    fun generateRandomCode(): String {
        val min = 0
        val max = 999_999
        val randomNumber = Random.nextInt(min, max + 1)
        return String.format("%06d", randomNumber)
    }

    private fun createEmailTemplate(content: String, emailType: EmailType): String =
        templateEngine.process(
            emailType.template,
            Context().apply { setVariable("content", content) }
        )

}