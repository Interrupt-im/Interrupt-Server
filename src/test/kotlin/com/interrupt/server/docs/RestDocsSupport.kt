package com.interrupt.server.docs

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsSupport {

    protected lateinit var mockMvc: MockMvc
    protected val objectMapper = ObjectMapper()

    companion object {
        @JvmStatic
        protected val BASE_RESPONSE_DOCS_DESCRIPTORS = listOf(
            PayloadDocumentation.fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                .description("응답 코드"),
            PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                .description("메시지"),
            PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                .description("응답 데이터")
        )

        fun createDocsResponseDescriptors(vararg descriptors: FieldDescriptor): List<FieldDescriptor> = buildList {
            addAll(BASE_RESPONSE_DOCS_DESCRIPTORS)
            addAll(descriptors)
        }
    }

    @BeforeEach
    fun setUp(provider: RestDocumentationContextProvider) {

        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
            .apply<StandaloneMockMvcBuilder?>(documentationConfiguration(provider))
            .build()
    }

    protected abstract fun initController(): Any
}