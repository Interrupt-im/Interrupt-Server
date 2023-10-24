package com.interrupt.server.common.exception

import com.interrupt.server.common.api.ExceptionResponse
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalRestControllerAdvice {

    @ExceptionHandler(InterruptServerException::class)
    fun globalServerExceptionHandler(e: InterruptServerException): ResponseEntity<ExceptionResponse<*>> =
        ResponseEntity(ExceptionResponse(e.errorCode.status.value(), e.errorCode, e.message, null), e.errorCode.status)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(e: MethodArgumentNotValidException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(ErrorCode.INVALID_INPUT_VALUE.message, e, ErrorCode.INVALID_INPUT_VALUE)
            .let { ex ->
                ResponseEntity(
                    ExceptionResponse(
                        ex.errorCode.status.value(),
                        ex.errorCode,
                        ex.message,
                        e.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
                    ),
                    ex.errorCode.status)
            }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun httpMessageNotReadableExceptionHandler(e: HttpMessageNotReadableException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(ErrorCode.NO_CONTENT_HTTP_BODY.message, e, ErrorCode.NO_CONTENT_HTTP_BODY)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.message, null), it.errorCode.status) }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun httpRequestMethodNotSupportedExceptionHandler(e: HttpRequestMethodNotSupportedException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(ErrorCode.NOT_SUPPORTED_METHOD.message, e, ErrorCode.NOT_SUPPORTED_METHOD)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.message, null), it.errorCode.status) }

    @ExceptionHandler(Throwable::class)
    fun throwableHandler(t: Throwable): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(cause = t, errorCode = ErrorCode.INTERNAL_SEVER_ERROR)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.message, null), it.errorCode.status) }

}