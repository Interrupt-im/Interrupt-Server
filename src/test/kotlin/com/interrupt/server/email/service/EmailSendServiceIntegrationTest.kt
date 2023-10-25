package com.interrupt.server.email.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.email.dto.EmailType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class EmailSendServiceIntegrationTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var emailSendService: EmailSendService

    @Test
    fun `이메일 타입에 알맞은 이메일 템플릿을 생성한다`() {
        // given
        val templateName = EmailType.MEMBER_REGISTER.template
        val code = "000000"
        val args = mapOf(("code" to code))
        val expectedTemplate = """
            <p> 아래 코드를 회원가입 창으로 돌아가 입력해주세요.</p>
            <br>

            <div align="center" style="border:1px solid black;">
                <h3> 회원가입 인증 코드 입니다. </h3>
                <div style="font-size:130%">$code</div>
            </div>
            <br/>
        """.trimIndent().replace(Regex("\\s+"), "")

        // when
        val template = emailSendService.generateEmailTemplate(templateName, args)

        // then
        assertThat(template.trim().replace(Regex("\\s+"), "")).isEqualTo(expectedTemplate)
    }

}