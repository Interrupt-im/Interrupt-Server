package com.interrupt.server.common.exception

import com.interrupt.server.common.api.ExceptionResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.naming.AuthenticationException

@RestControllerAdvice
class GlobalRestControllerAdvice {

    @ExceptionHandler(InterruptServerException::class)
    fun handleGlobalServerException(e: InterruptServerException): ResponseEntity<ExceptionResponse<*>> =
        ResponseEntity(ExceptionResponse(e.errorCode.status.value(), e.errorCode, e.message, null), e.errorCode.status)

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(cause = e, errorCode = ErrorCode.UNAUTHORIZED)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.errorCode.message, null), it.errorCode.status) }

    @ExceptionHandler(InsufficientAuthenticationException::class)
    fun handleInsufficientAuthenticationException(e: InsufficientAuthenticationException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(cause = e, errorCode = ErrorCode.UNAUTHORIZED)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.errorCode.message, null), it.errorCode.status) }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(cause = e, errorCode = ErrorCode.FORBIDDEN)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.errorCode.message, null), it.errorCode.status) }

    @ExceptionHandler(SignatureException::class)
    fun handleSignatureException(e: SignatureException) =
        InterruptServerException(cause = e, errorCode = ErrorCode.INVALID_TOKEN)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.errorCode.message, null), it.errorCode.status) }

    @ExceptionHandler(MalformedJwtException::class)
    fun handleMalformedJwtException(e: MalformedJwtException) =
        InterruptServerException(cause = e, errorCode = ErrorCode.INVALID_TOKEN)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.errorCode.message, null), it.errorCode.status) }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(e: ExpiredJwtException) =
        InterruptServerException(cause = e, errorCode = ErrorCode.EXPIRED_TOKEN)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.errorCode.message, null), it.errorCode.status) }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(ErrorCode.INVALID_INPUT_VALUE, ErrorCode.INVALID_INPUT_VALUE.message, e,)
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

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(ErrorCode.INVALID_INPUT_VALUE, ErrorCode.INVALID_INPUT_VALUE.message, e)
            .let { ex ->
                ResponseEntity(
                    ExceptionResponse(
                        ex.errorCode.status.value(),
                        ex.errorCode,
                        ex.message,
                        e.constraintViolations.associate { (it.propertyPath.last()) to it.message }
                ),
                ex.errorCode.status)
            }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(ErrorCode.NO_CONTENT_HTTP_BODY, ErrorCode.NO_CONTENT_HTTP_BODY.message, e)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.message, null), it.errorCode.status) }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(ErrorCode.NOT_SUPPORTED_METHOD, ErrorCode.NOT_SUPPORTED_METHOD.message, e)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.message, null), it.errorCode.status) }

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(t: Throwable): ResponseEntity<ExceptionResponse<*>> =
        InterruptServerException(cause = t, errorCode = ErrorCode.INTERNAL_SEVER_ERROR)
            .let { ResponseEntity(ExceptionResponse(it.errorCode.status.value(), it.errorCode, it.message, null), it.errorCode.status) }

}