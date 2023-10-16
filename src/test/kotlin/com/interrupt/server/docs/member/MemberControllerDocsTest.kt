package com.interrupt.server.docs.member

import com.interrupt.server.docs.RestDocsSupport
import com.interrupt.server.member.api.MemberController
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckResponse
import com.interrupt.server.member.service.MemberService
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MemberControllerDocsTest: RestDocsSupport() {

    companion object {

        @BeforeAll
        @JvmStatic
        fun setUpAll() {
            MockKAnnotations.init(this)
        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {
            clearAllMocks()
        }

    }

    private val memberService: MemberService = mockk<MemberService>()

    override fun initController(): Any = MemberController(memberService)

    @Test
    fun `회원 ID 중복 체크 API`() {
        val request = LoginIdDuplicateCheckRequest("testLoginId")

        every { memberService.checkLoginIdDuplication(any<LoginIdDuplicateCheckRequest>()) } returns
                LoginIdDuplicateCheckResponse(true)

        val result = mockMvc.perform(
            get("/api/v1/members/check-login-id")
                .param("loginId", request.loginId)
        )

        result
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("auth-check-login-id-duplication",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("loginId").description("회원 ID"),
                ),
                responseFields(
                    createDocsResponseDescriptors(
                        fieldWithPath("data.isUnique").type(JsonFieldType.BOOLEAN)
                            .description("중복 여부")
                    )
                )
            ))
    }

}