package com.interrupt.server.auth.api

import com.interrupt.server.ControllerTestSupport
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyRequest
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyResponse
import com.interrupt.server.member.dto.emailverify.EmailVerifyRequest
import com.interrupt.server.auth.dto.login.SignInRequest
import com.interrupt.server.auth.dto.login.SignInResponse
import com.interrupt.server.member.dto.recover.*
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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

    @Test
    fun `이메일 인증 코드 발송 요청 시 이메일 주소는 필수 값 이다`() {
        // given
        val request = EmailVerificationApplyRequest("")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/verify-code")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("email", "이메일 값은 필수 입니다.")
    }

    @Test
    fun `이메일 인증 코드 발송 요청 시 이메일 주소는 올바른 형식 이어야 한다`() {
        // given
        val request = EmailVerificationApplyRequest("email")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/verify-code")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("email", "올바른 이메일 형식으로 입력하셔야 합니다.")
    }

    @Test
    fun `이메일 인증 코드 확인`() {
        // given
        val request = EmailVerifyRequest("email@domail.com", "0000", "000000")

        justRun { memberService.validateEmailVerifyCode(request) }

        // when then
        mockMvc.perform(
            post("/api/v1/auth/verification/verify-code")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent)
    }

    @Test
    fun `이메일 인증 코드 확인 시 이메일 값은 필수 값 이다`() {
        // given
        val request = EmailVerifyRequest("", "000000", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/verification/verify-code")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("email", "이메일 값은 필수 입니다.")
    }

    @Test
    fun `이메일 인증 코드 확인 시 이메일은 올바른 형식이어야 한다`() {
        // given
        val request = EmailVerifyRequest("email", "000000", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/verification/verify-code")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("email", "올바른 이메일 형식으로 입력하셔야 합니다.")
    }

    @Test
    fun `이메일 인증 코드 확인 시 이메일 인증 코드는 필수 값 이다`() {
        // given
        val request = EmailVerifyRequest("email@domail.com", "0000", null)

        justRun { memberService.validateEmailVerifyCode(request) }

        // when then
        mockMvc.perform(
            post("/api/v1/auth/verification/verify-code")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("verifyCode", "이메일 인증코드 값은 필수 입니다.")
    }

    @Test
    fun `이메일 인증 코드 확인 시 이메일 인증 코드는 6자 이다`() {
        // given
        val request = EmailVerifyRequest("email@domail.com", "0000", "00000")

        justRun { memberService.validateEmailVerifyCode(request) }

        // when then
        mockMvc.perform(
            post("/api/v1/auth/verification/verify-code")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("verifyCode", "이메일 인증코드는 6자리 입니다.")
    }

    @Test
    fun `이메일 인증 코드 확인 시 이메일 인증 코드는 숫자로만 이루어져야 한다`() {
        // given
        val request = EmailVerifyRequest("email@domail.com", "0000", "11111q")

        justRun { memberService.validateEmailVerifyCode(request) }

        // when then
        mockMvc.perform(
            post("/api/v1/auth/verification/verify-code")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("verifyCode", "이메일 인증코드는 숫자만으로 입력하셔야 합니다.")
    }

    @Test
    @WithMockUser
    fun `로그인`() {
        // given
        val request = SignInRequest("memberId", "password123!")
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"

        every { authService.login(request) } returns SignInResponse(accessToken, refreshToken)

        // when then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isOkBaseResponse()
            .andExpect(jsonPath("$.data.accessToken").value(accessToken))
            .andExpect(jsonPath("$.data.refreshToken").value(refreshToken))
    }

    @Test
    fun `로그인 시 회원 ID 는 필수 값 이다`() {
        // given
        val request = SignInRequest(null, "ward123!")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("loginId", "아이디 값은 필수 입니다.")
    }

    @Test
    fun `로그인 시 회원 ID 는 8~20자로 이루어져아 한다`() {
        // given
        val request = SignInRequest("loginId", "ward123!")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("loginId", "아이디는 8자 이상 20자 이하로 설정해야 합니다.")
    }

    @Test
    fun `로그인 시 회원 ID 는 영문 또는 숫자로 이루어져야 한다`() {
        // given
        val request = SignInRequest("회원아이디123", "ward123!")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("loginId", "아이디는 영어(필수)와 숫자로 설정해야 합니다.")
    }

    @Test
    fun `로그인 시 비밀번호 는 필수 값 이다`() {
        // given
        val request = SignInRequest("memberId", null)

        // when then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password", "비밀번호 값은 필수 입니다.")
    }

    @Test
    fun `로그인 시 비밀번호 는 8~20자로 이루어져아 한다`() {
        // given
        val request = SignInRequest("memberId", "word123")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password", "비밀번호는 8자 이상 20자 이하로 설정해야 합니다.")
    }

    @Test
    fun `로그인 시 비밀번호 는 영문과 숫자가 모두 포함되어야 한다`() {
        // given
        val request = SignInRequest("memberId", "password")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password", "비밀번호는 영어(필수), 숫자(필수), 특수문자(선택)로 설정해야 합니다.")
    }

    @Test
    fun `회원 ID 찾기 이메일 인증 코드 발송 요청`() {
        // given
        val request = RecoverLoginIdRequest("홍길동", "email@domain.com")

        every { memberService.applySendLoginIdRecoverVerifyCode(request) } returns RecoverLoginIdResponse("0000")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/login-id/recovery/verify-code")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isOkBaseResponse()
            .andExpect(jsonPath("$.data").isNotEmpty)
    }

    @Test
    fun `회원 ID 찾기 이메일 인증 코드 발송 요청 시 이름 값은 필수이다`() {
            // given
            val request = RecoverLoginIdRequest("", "email@domain.com")

            // when then
            mockMvc.perform(
                post("/api/v1/auth/login-id/recovery/verify-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .isInvalidInputValueResponse("name", "이름 값은 필수 입니다.")
    }

    @Test
    fun `회원 ID 찾기 이메일 인증 코드 발송 요청 시 이메일 값은 필수이다`() {
            // given
            val request = RecoverLoginIdRequest("홍길동", "")

            // when then
            mockMvc.perform(
                post("/api/v1/auth/login-id/recovery/verify-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .isInvalidInputValueResponse("email", "이메일 값은 필수 입니다.")
    }

    @Test
    fun `회원 ID 찾기 이메일 인증 코드 발송 요청 시 이메일 값은 올바른 형식 이어야 한다`() {
            // given
            val request = RecoverLoginIdRequest("홍길동", "email")

            // when then
            mockMvc.perform(
                post("/api/v1/auth/login-id/recovery/verify-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .isInvalidInputValueResponse("email", "올바른 이메일 형식으로 입력하셔야 합니다.")
    }

    @Test
    fun `회원 ID 찾기 이메일 인증 코드 확인`() {
        // given
        val request = VerifyRecoverLoginIdRequest("0000", "000000")
        val loginId = "memberId"

        every { memberService.validateLoginIdRecoverVerifyCode(request) } returns VerifyRecoverLoginIdResponse(loginId)

        // when then
        mockMvc.perform(
            get("/api/v1/auth/login-id")
                .param("memberRecoverKey", request.memberRecoverKey)
                .param("verifyCode", request.verifyCode))
            .andDo(print())
            .isOkBaseResponse()
            .andExpect(jsonPath("$.data").isNotEmpty)
            .andExpect(jsonPath("$.data.loginId").value(loginId))
    }

    @Test
    fun `회원 ID 찾기 이메일 인증 코드 확인 시 이메일 인증 코드 값은 필수이다`() {
        // given
        val request = VerifyRecoverLoginIdRequest("0000", null)

        // when then
        mockMvc.perform(
            get("/api/v1/auth/login-id")
                .param("memberRecoverKey", request.memberRecoverKey)
                .param("verifyCode", request.verifyCode))
            .andDo(print())
            .isInvalidInputValueResponse("verifyCode", "이메일 인증코드 값은 필수 입니다.")
    }

    @Test
    fun `회원 ID 찾기 이메일 인증 코드 확인 시 이메일 인증 코드 값은 6자 이다`() {
        // given
        val request = VerifyRecoverLoginIdRequest("0000", "00000")

        // when then
        mockMvc.perform(
            get("/api/v1/auth/login-id")
                .param("memberRecoverKey", request.memberRecoverKey)
                .param("verifyCode", request.verifyCode))
            .andDo(print())
            .isInvalidInputValueResponse("verifyCode", "이메일 인증코드는 6자리 입니다.")
    }

    @Test
    fun `회원 ID 찾기 이메일 인증 코드 확인 시 이메일 인증 코드 값은 숫자로만 이루어져야 한다`() {
        // given
        val request = VerifyRecoverLoginIdRequest("0000", "11111q")

        // when then
        mockMvc.perform(
            get("/api/v1/auth/login-id")
                .param("memberRecoverKey", request.memberRecoverKey)
                .param("verifyCode", request.verifyCode))
            .andDo(print())
            .isInvalidInputValueResponse("verifyCode", "이메일 인증코드는 숫자만으로 입력하셔야 합니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 발송 요청`() {
        // given
        val request = RecoverPasswordRequest("memberId", "email@domain.com")

        every { memberService.applySendPasswordRecoverVerifyCode(request) } returns RecoverPasswordResponse("0000")

        // when then
        mockMvc.perform(
            post("/api/v1/auth/password/recovery/verify-code")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isOkBaseResponse()
            .andExpect(jsonPath("$.data").isNotEmpty)
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 발송 요청 시 회원 ID 값은 필수이다`() {
            // given
        val request = RecoverPasswordRequest(null, "email@domain.com")

            // when then
            mockMvc.perform(
                post("/api/v1/auth/password/recovery/verify-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .isInvalidInputValueResponse("loginId", "아이디 값은 필수 입니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 발송 요청 시 회원 ID 값은 8~20자로 이루어져아 한다`() {
            // given
        val request = RecoverPasswordRequest("loginId", "email@domain.com")

            // when then
            mockMvc.perform(
                post("/api/v1/auth/password/recovery/verify-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .isInvalidInputValueResponse("loginId", "아이디는 8자 이상 20자 이하로 설정해야 합니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 발송 요청 시 회원 ID 값은 영문 또는 숫자로 이루어져야 한다`() {
            // given
        val request = RecoverPasswordRequest("회원아이디123", "email@domain.com")

            // when then
            mockMvc.perform(
                post("/api/v1/auth/password/recovery/verify-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .isInvalidInputValueResponse("loginId", "아이디는 영어(필수)와 숫자로 설정해야 합니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 발송 요청 시 이메일 값은 필수이다`() {
            // given
        val request = RecoverPasswordRequest("memberId", "")

            // when then
            mockMvc.perform(
                post("/api/v1/auth/password/recovery/verify-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .isInvalidInputValueResponse("email", "이메일 값은 필수 입니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 발송 요청 시 이메일 값은 올바른 형식 이어야 한다`() {
            // given
        val request = RecoverPasswordRequest("memberId", "email")

            // when then
            mockMvc.perform(
                post("/api/v1/auth/password/recovery/verify-code")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .isInvalidInputValueResponse("email", "올바른 이메일 형식으로 입력하셔야 합니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인`() {
        // given
        val request = VerifyRecoverPasswordRequest("0000", "000000", "newP123!")

        justRun { memberService.validatePasswordRecoverVerifyCode(request) }

        // when then
        mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "memberId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent)
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인 시 회원 ID 는 8~20자로 이루어져아 한다`() {
        // given
        val request = VerifyRecoverPasswordRequest("0000", "000000", "newP123!")

        justRun { memberService.validatePasswordRecoverVerifyCode(request) }

        // when then
        mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "loginId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("loginId", "아이디는 8자 이상 20자 이하로 설정해야 합니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인 시 회원 ID 는 영문 또는 숫자로 이루어져야 한다`() {
        // given
        val request = VerifyRecoverPasswordRequest("0000", "000000", "newP123!")

        justRun { memberService.validatePasswordRecoverVerifyCode(request) }

        // when then
        mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "회원아이디123")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("loginId", "아이디는 영어(필수)와 숫자로 설정해야 합니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인 시 이메일 인증 코드 값은 필수이다`() {
        // given
        val request = VerifyRecoverPasswordRequest("0000", null, "word123!")

        // when then
        mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "memberId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("verifyCode", "이메일 인증코드 값은 필수 입니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인 시 이메일 인증 코드 값은 6자 이다`() {
        // given
        val request = VerifyRecoverPasswordRequest("0000", "00000", "word123!")

        // when then
        mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "memberId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("verifyCode", "이메일 인증코드는 6자리 입니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인 시 이메일 인증 코드 값은 숫자로만 이루어져야 한다`() {
        // given
        val request = VerifyRecoverPasswordRequest("0000", "11111q", "word123!")

        // when then
        mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "memberId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("verifyCode", "이메일 인증코드는 숫자만으로 입력하셔야 합니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인 시 비밀번호 값은 필수이다`() {
        // given
        val request = VerifyRecoverPasswordRequest("0000", "000000", null)

        // when then
        mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "memberId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password", "비밀번호 값은 필수 입니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인 시 비밀번호 는 8~20자로 이루어져아 한다`() {
        // given
        val request = VerifyRecoverPasswordRequest("0000", "000000", "word123")

        // when then
        mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "memberId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password", "비밀번호는 8자 이상 20자 이하로 설정해야 합니다.")
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인 시 비밀번호 는 영문과 숫자가 모두 포함되어야 한다`() {
        // given
        val request = VerifyRecoverPasswordRequest("0000", "000000", "password")

        // when then
        mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "memberId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password", "비밀번호는 영어(필수), 숫자(필수), 특수문자(선택)로 설정해야 합니다.")
    }

}