package com.interrupt.server.docs.auth

import com.interrupt.server.auth.api.AuthController
import com.interrupt.server.docs.ConstrainedFields
import com.interrupt.server.docs.ConstrainedPath
import com.interrupt.server.docs.RestDocsSupport
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyRequest
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyResponse
import com.interrupt.server.member.dto.emailverify.EmailVerifyRequest
import com.interrupt.server.member.dto.login.MemberLoginRequest
import com.interrupt.server.member.dto.login.MemberLoginResponse
import com.interrupt.server.member.dto.recover.*
import com.interrupt.server.member.service.MemberService
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerDocsTest: RestDocsSupport() {

    private val memberService: MemberService = mockk<MemberService>()

    override fun initController(): Any = AuthController(memberService)

    @Test
    fun `이메일 인증 코드 요청 API`() {
        val request = EmailVerificationApplyRequest("email@doamin.com")
        val fields = ConstrainedFields(request::class.java)

        every { memberService.applySendEmailVerifyCode(any<EmailVerificationApplyRequest>()) } returns
                EmailVerificationApplyResponse("0000")

        val result = mockMvc.perform(
            post("/api/v1/auth/verify-code")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        result
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(document(
                "apply-email-verify-code",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fields.withPath("email").type(JsonFieldType.STRING)
                        .description("이메일")
                ),
                responseFields(
                    createDocsResponseDescriptors(
                        fieldWithPath("data.emailVerifyCodeKey").type(JsonFieldType.STRING)
                            .description("이메일 인증 키")
                    )
                )
            ))
    }

    @Test
    fun `이메일 인증 코드 확인 API`() {
        val request = EmailVerifyRequest("email@domain.com", "0000", "000000")
        val fields = ConstrainedFields(request::class.java)

        justRun { memberService.validateEmailVerifyCode(request) }

        val result = mockMvc.perform(
            post("/api/v1/auth/verification/verify-code")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        result
            .andDo(print())
            .andExpect(status().isNoContent)
            .andDo(document(
                "validate-email-verify-code",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fields.withPath("email").type(JsonFieldType.STRING)
                        .description("이메일"),
                    fields.withPath("emailVerifyCodeKey").type(JsonFieldType.STRING)
                        .description("이메일 인증 키"),
                    fields.withPath("verifyCode").type(JsonFieldType.STRING)
                        .description("이메일 인증 코드")
                )
            ))
    }

    @Test
    fun `로그인 API`() {
        val request = MemberLoginRequest("memberId", "password123")
        val fields = ConstrainedFields(request::class.java)

        every { memberService.login(any<MemberLoginRequest>()) } returns
                MemberLoginResponse("name")

        val result = mockMvc.perform(
            post("/api/v1/auth/login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        result
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(document(
                "login",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fields.withPath("loginId").type(JsonFieldType.STRING)
                        .description("회원 ID"),
                    fields.withPath("password").type(JsonFieldType.STRING)
                        .description("비밀번호")
                ),
                responseFields(
                    createDocsResponseDescriptors(
                        fieldWithPath("data.name").type(JsonFieldType.STRING)
                            .description("회원 이름")
                    )
                )
            ))
    }

    @Test
    fun `아이디 찾기 이메일 인증 코드 요청 API`() {
        val request = RecoverLoginIdRequest("name", "email@domain.com")
        val fields = ConstrainedFields(request::class.java)

        every { memberService.applySendLoginIdRecoverVerifyCode(request) } returns
                RecoverLoginIdResponse("0000")

        val result = mockMvc.perform(
            post("/api/v1/auth/login-id/recovery/verify-code")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        result
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(document(
                "apply-login-id-recover-verify-code",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fields.withPath("name").type(JsonFieldType.STRING)
                        .description("회원 이름"),
                    fields.withPath("email").type(JsonFieldType.STRING)
                        .description("이메일")
                ),
                responseFields(
                    createDocsResponseDescriptors(
                        fieldWithPath("data.memberRecoverKey").type(JsonFieldType.STRING)
                            .description("회원 정보 찾기 인증 키")
                    )
                )
            ))
    }

    @Test
    fun `회원 ID 찾기 이메일 인증 코드 확인 API`() {
        val request = VerifyRecoverLoginIdRequest("0000", "000000")
        val fields = ConstrainedFields(request::class.java)

        every { memberService.validateLoginIdRecoverVerifyCode(request) } returns
                VerifyRecoverLoginIdResponse("memberId")

        val result = mockMvc.perform(
            get("/api/v1/auth/login-id")
                .param("memberRecoverKey", request.memberRecoverKey)
                .param("verifyCode", request.verifyCode)
        )

        result
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(document(
                "recover-login-id",
                getDocumentRequest(),
                getDocumentResponse(),
                queryParameters(
                    fields.withName("memberRecoverKey").description("회원 정보 찾기 인증 키"),
                    fields.withName("verifyCode").description("이메일 인증 코드")
                ),
                responseFields(
                    createDocsResponseDescriptors(
                        fieldWithPath("data.loginId").type(JsonFieldType.STRING)
                            .description("회원 ID")
                    )
                )
            ))
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 요청 API`() {
        val request = RecoverPasswordRequest("memberId", "email@domain.com")
        val fields = ConstrainedFields(request::class.java)

        every { memberService.applySendPasswordRecoverVerifyCode(request) } returns
                RecoverPasswordResponse("0000")

        val result = mockMvc.perform(
            post("/api/v1/auth/password/recovery/verify-code")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        result
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(document(
                "apply-password-recover-verify-code",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fields.withPath("loginId").type(JsonFieldType.STRING)
                        .description("회원 ID"),
                    fields.withPath("email").type(JsonFieldType.STRING)
                        .description("이메일")
                ),
                responseFields(
                    createDocsResponseDescriptors(
                        fieldWithPath("data.memberRecoverKey").type(JsonFieldType.STRING)
                            .description("회원 정보 찾기 인증 키")
                    )
                )
            ))
    }

    @Test
    fun `비밀번호 찾기 이메일 인증 코드 확인 API`() {
        val request = VerifyRecoverPasswordRequest("0000", "000000", "newPassword123")
        val fields = ConstrainedFields(request::class.java)
        val paths = ConstrainedPath(AuthController::validatePasswordRecoverVerifyCode)

        justRun { memberService.validatePasswordRecoverVerifyCode(request) }

        val result = mockMvc.perform(
            put("/api/v1/auth/member/{loginId}/password", "memberId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        result
            .andDo(print())
            .andExpect(status().isNoContent)
            .andDo(document(
                "recover-password",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    paths.withName("loginId").description("회원 ID")
                ),
                requestFields(
                    fields.withPath("memberRecoverKey").type(JsonFieldType.STRING)
                        .description("회원 정보 찾기 인증 키"),
                    fields.withPath("verifyCode").type(JsonFieldType.STRING)
                        .description("이메일 인증 코드"),
                    fields.withPath("password").type(JsonFieldType.STRING)
                        .description("변경 비밀번호")
                )
            ))
    }

}