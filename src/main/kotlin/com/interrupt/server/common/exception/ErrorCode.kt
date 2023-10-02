package com.interrupt.server.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
    val code: String,
) {

    // MEMBER
    DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다.", "ME0001"),

    // COMMON
    NOT_SALTED_STRING(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의 바랍니다.", "IE0001"),
    INTERNAL_SEVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의 바랍니다.", "IE0002"),
}