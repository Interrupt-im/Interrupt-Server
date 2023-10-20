package com.interrupt.server.docs.member

import com.interrupt.server.docs.ConstrainedFields
import com.interrupt.server.docs.RestDocsSupport
import com.interrupt.server.member.api.MemberController
import com.interrupt.server.member.dto.delete.MemberDeleteRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckResponse
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.dto.update.MemberUpdateRequest
import com.interrupt.server.member.service.MemberService
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MemberControllerDocsTest: RestDocsSupport() {

    private val memberService: MemberService = mockk<MemberService>()

    override fun initController(): Any = MemberController(memberService)

    @Test
    fun `회원 ID 중복 체크 API`() {
        val request = LoginIdDuplicateCheckRequest("testLoginId")
        val fields = ConstrainedFields(request::class.java)

        every { memberService.checkLoginIdDuplication(any<LoginIdDuplicateCheckRequest>()) } returns
                LoginIdDuplicateCheckResponse(true)

        val result = mockMvc.perform(
            get("/api/v1/members/check-login-id")
                .param("loginId", request.loginId))

        result
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(
                document(
                    "check-login-id-duplication",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    queryParameters(
                        fields.withName("loginId").description("회원 ID")
                    ),
                    responseFields(
                        createDocsResponseDescriptors(
                            fieldWithPath("data.isUnique").type(JsonFieldType.BOOLEAN)
                                .description("중복 여부"))
                    )
                ))
    }

    @Test
    fun `회원 가입 API`() {
        val request = MemberRegisterRequest("testLoginId", "password123", "홍길동", "test@test.com", "0000")
        val fields = ConstrainedFields(request::class.java)

        justRun { memberService.registerMember(any<MemberRegisterRequest>()) }

        val result = mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))

        result
            .andDo(print())
            .andExpect(status().isCreated)
            .andDo(document("sign-in",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fields.withPath("loginId").type(JsonFieldType.STRING)
                        .description("회원 ID"),
                    fields.withPath("password").type(JsonFieldType.STRING)
                        .description("비밀번호"),
                    fields.withPath("name").type(JsonFieldType.STRING)
                        .description("회원 이름"),
                    fields.withPath("email").type(JsonFieldType.STRING)
                        .description("이메일"),
                    fields.withPath("emailVerifyCodeKey").type(JsonFieldType.STRING)
                        .description("이메일 인증 키"),
                ),
                responseFields(createDocsResponseDescriptors())
            ))
    }

    @Test
    fun `회원 수정 API`() {
        val request = MemberUpdateRequest("password123", "홍길동", "test@test.com", "0000")
        val fields = ConstrainedFields(request::class.java)

        justRun { memberService.updateMember(any<String>(), any<MemberUpdateRequest>()) }

        val result = mockMvc.perform(
            patch("/api/v1/members/{loginId}", "loginId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))

        result
            .andDo(print())
            .andExpect(status().isNoContent)
            .andDo(document("update-member",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    fields.withName("loginId").description("회원 ID")),
                requestFields(
                    fields.withPath("password").type(JsonFieldType.STRING)
                        .optional()
                        .description("비밀번호"),
                    fields.withPath("name").type(JsonFieldType.STRING)
                        .optional()
                        .description("회원 이름"),
                    fields.withPath("email").type(JsonFieldType.STRING)
                        .optional()
                        .description("이메일"),
                    fields.withPath("emailVerifyCodeKey").type(JsonFieldType.STRING)
                        .optional()
                        .description("이메일 인증 키"),
                ),
            ))
    }

    @Test
    fun `회원 탈퇴 API`() {
        val request = MemberDeleteRequest("password123")
        val fields = ConstrainedFields(request::class.java)

        justRun { memberService.deleteMember(any<String>(), any<MemberDeleteRequest>()) }

        val result = mockMvc.perform(
            delete("/api/v1/members/{loginId}", "loginId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )

        result
            .andDo(print())
            .andExpect(status().isNoContent)
            .andDo(document("delete-member",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    fields.withName("loginId").description("회원 ID")),
                requestFields(
                    fields.withPath("password").type(JsonFieldType.STRING)
                        .optional()
                        .description("비밀번호"),
                ),
            ))
    }

}