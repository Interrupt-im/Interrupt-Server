package com.interrupt.server.docs.exception

import com.interrupt.server.common.api.ExceptionResponse
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.docs.RestDocsSupport
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootTest
class ExceptionHandlerDocsTest: RestDocsSupport() {

    @RestController
    class ExceptionDocController {
        @PostMapping("/api/v1/exception/invalid-input")
        fun invalidInputException(): ResponseEntity<ExceptionResponse<*>> =
            ResponseEntity(
                ExceptionResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    ErrorCode.INVALID_INPUT_VALUE,
                    ErrorCode.INVALID_INPUT_VALUE.message,
                    buildMap { put("field1", "공백일 수 없습니다."); put("field2", "8자 이상 20자 이하 값을 입력하셔야 합니다.") }
                ),
                HttpStatus.BAD_REQUEST)
        @PostMapping("/api/v1/exception/integration")
        fun integrationException(): ResponseEntity<ExceptionResponse<*>> =
            ResponseEntity(
                ExceptionResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    ErrorCode.INVALID_INPUT_VALUE,
                    ErrorCode.INVALID_INPUT_VALUE.message,
                    null
                ),
                HttpStatus.BAD_REQUEST)
    }

    override fun initController(): Any = ExceptionDocController()


    private val errorResponseDocsDescriptors = listOf(
        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
            .description("응답 코드"),
        fieldWithPath("errorCode").type(JsonFieldType.STRING)
            .description("에러 코드"),
        fieldWithPath("message").type(JsonFieldType.STRING)
            .description("메시지"),
        fieldWithPath("data").type(JsonFieldType.OBJECT)
            .optional()
            .description("응답 데이터")
    )

    private fun createDocsErrorResponseDescriptors(vararg descriptors: FieldDescriptor): List<FieldDescriptor> = buildList {
        addAll(errorResponseDocsDescriptors)
        addAll(descriptors)
    }

    @Test
    fun `입력값 검증 예외`() {
        val result = mockMvc.perform(
            post("/api/v1/exception/invalid-input")
        )

        result
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(document(
                "input-validation-error",
                getDocumentResponse(),
                responseFields(
                    createDocsErrorResponseDescriptors(
                        fieldWithPath("data.field1").type(JsonFieldType.STRING).optional().description("필드 별 검증 실패 메시지"),
                        fieldWithPath("data.field2").type(JsonFieldType.STRING).optional().description("필드 별 검증 실패 메시지")
                    )),
                ))
    }

    @Test
    fun `통합 예외`() {
        val result = mockMvc.perform(
            post("/api/v1/exception/integration")
        )

        result
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(document(
                "integration-error",
                getDocumentResponse(),
                responseFields(
                    createDocsErrorResponseDescriptors()),
                ))
    }
}