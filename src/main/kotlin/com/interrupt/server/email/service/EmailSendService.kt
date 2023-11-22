package com.interrupt.server.email.service

import com.interrupt.server.email.entity.EmailMessage
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class EmailSendService(
    private val javaMailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
) {

    private val log: KLogger = KotlinLogging.logger {  }

    fun sendMail(emailMessage: EmailMessage) {
        val mimeMessage = javaMailSender.createMimeMessage()

        MimeMessageHelper(mimeMessage, false, "UTF-8").apply {
            setTo(emailMessage.to)
            setSubject(emailMessage.emailContents.subject)
            setText(emailMessage.emailContents.content, true)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                javaMailSender.send(mimeMessage)
            } catch (t: Throwable) {
                log.error {
                    """
                        sendMail failed
                        content: $emailMessage
                        message: ${t.message}
                        cause: ${t.cause}
                    """.trimIndent()
                }
            }
        }
    }

    fun generateEmailTemplate(templateName: String, args: Map<String, Any>): String =
        templateEngine.process(
            templateName,
            Context().apply {
                args.forEach { (key, value) -> setVariable(key, value) }
            }
        )

}