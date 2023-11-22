package com.interrupt.server.common.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.interrupt.server.common.exception.ErrorCode
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExceptionResponse<T>(
    val statusCode: Int = HttpStatus.OK.value(),
    val errorCode: ErrorCode,
    val message: String,
    val data: T
) {
}