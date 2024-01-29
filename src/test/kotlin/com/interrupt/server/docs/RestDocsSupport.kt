package com.interrupt.server.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.interrupt.server.auth.web.LoginMember
import com.interrupt.server.member.entity.Member
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.MethodParameter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsSupport {

    protected lateinit var mockMvc: MockMvc
    protected val objectMapper = ObjectMapper()

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

        val memberArgumentResolver = object : HandlerMethodArgumentResolver {
            override fun supportsParameter(parameter: MethodParameter): Boolean {
                val hasAnnotation = parameter.hasParameterAnnotation(LoginMember::class.java)
                val isMemberType = Member::class.java.isAssignableFrom(parameter.parameterType)

                return (hasAnnotation && isMemberType)
            }

            override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? =
                Member("loginId", "password", "name", "email@interrupt.im")
        }

        this.mockMvc = MockMvcBuilders
            .standaloneSetup(initController())
            .setCustomArgumentResolvers(memberArgumentResolver)
            .apply<StandaloneMockMvcBuilder?>(documentationConfiguration(provider))
            .build()
    }

    protected abstract fun initController(): Any

    protected fun getDocumentRequest(): OperationRequestPreprocessor =
        preprocessRequest(
            modifyUris()
                .scheme("https")
                .host("api.interrupt.com")
                .removePort(),
            prettyPrint()
        )

    protected fun getDocumentResponse(): OperationResponsePreprocessor =
        preprocessResponse(prettyPrint())

    protected fun header(): Array<HeaderDescriptor> =
        arrayOf(headerWithName("x-api-key").description("Api Key"))


}