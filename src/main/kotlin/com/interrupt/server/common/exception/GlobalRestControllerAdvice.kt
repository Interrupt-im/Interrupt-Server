package com.interrupt.server.common.exception

import com.interrupt.server.common.api.BaseResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalRestControllerAdvice {

    @ExceptionHandler(InterruptServerException::class)
    fun globalServerExceptionHandler(e: InterruptServerException): ResponseEntity<BaseResponse<*>> =
        ResponseEntity(BaseResponse(e.errorCode.status.value(), e.errorCode.status, e.errorCode.code), e.errorCode.status)

    @ExceptionHandler(Throwable::class)
    fun throwableHandler(t: Throwable): ResponseEntity<BaseResponse<*>> =
        InterruptServerException(cause = t, errorCode = ErrorCode.INTERNAL_SEVER_ERROR).let { ResponseEntity(BaseResponse(it.errorCode.status.value(), it.errorCode.status, it.errorCode.code), it.errorCode.status) }

}