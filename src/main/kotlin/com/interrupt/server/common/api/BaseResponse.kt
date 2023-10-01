package com.interrupt.server.common.api

import org.springframework.http.HttpStatus

data class BaseResponse<T>(
    val statusCode: Int,
    val status: HttpStatus,
    val result: T
) {
}