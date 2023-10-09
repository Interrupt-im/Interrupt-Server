package com.interrupt.server.member.repository

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.member.entity.EmailVerifyCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class EmailVerifyCodeRepositoryTestMail: IntegrationTestSupport() {

    @Autowired
    private lateinit var emailVerifyCodeRepository: EmailVerifyCodeRepository

    @Test
    fun `이메일 인증코드를 저장한다`() {
        // given
        val verifyCode = EmailVerifyCode("test@test.com", "000000")

        // when
        val savedEmailVerifyCode = emailVerifyCodeRepository.save(verifyCode)

        // then
        assertThat(savedEmailVerifyCode).isEqualTo(verifyCode)
        assertThat(savedEmailVerifyCode.uuid).isNotNull()
    }

    @Test
    fun `uuid 를 이용해 이메일 인증코드를 반환받는다`() {
        // given
        val verifyCode = EmailVerifyCode("test@test.com", "000000")
        emailVerifyCodeRepository.save(verifyCode)

        // when
        val foundVerifyCode = emailVerifyCodeRepository.findByUuid(verifyCode.uuid)

        // then
        assertThat(foundVerifyCode).isEqualTo(verifyCode)
    }

}