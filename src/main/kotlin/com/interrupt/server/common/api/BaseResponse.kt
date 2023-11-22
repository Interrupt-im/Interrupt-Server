package com.interrupt.server.common.api

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseResponse<T>(
    val statusCode: Int = HttpStatus.OK.value(),
    val message: String = "success",
    val data: T
) {
}