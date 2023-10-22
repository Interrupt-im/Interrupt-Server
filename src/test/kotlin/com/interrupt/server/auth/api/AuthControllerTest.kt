package com.interrupt.server.auth.api

import com.interrupt.server.ControllerTestSupport
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyRequest
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyResponse
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

class AuthControllerTest: ControllerTestSupport() {

    @Test
    fun `이메일 인증 코드 발송 요청`() {
        // given
        val request = EmailVerificationApplyRequest("email@doamil.com")

        every { memberService.applySendEmailVerifyCode(request) } returns EmailVerificationApplyResponse("0000")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/verify-code")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isOkBaseResponse()
    }

}