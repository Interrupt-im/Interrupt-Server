package com.interrupt.server.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
    val code: String,
) {

    // MEMBER
    DUPLICATED_REGISTER_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디 입니다.", "ME0001"),
    FAILED_LOGIN(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 확인해 주세요.", "ME0002"),
    EMAIL_VERIFY_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "이메일을 인증할 수 없습니다. 다시 시도해 주세요.", "ME0003"),
    INVALID_EMAIL_VERIFY_CODE(HttpStatus.BAD_REQUEST, "이메일 인증 코드를 잘못 입력하였습니다.", "ME0004"),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "인증된 이메일이 아닙니다.", "ME0005"),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "일치하는 회원 정보를 찾을 수 없습니다.", "ME0006"),

    // Validation
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다.", "VE0001"),

    // COMMON
    INTERNAL_SEVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의 바랍니다.", "IE0002"),
    ;

    override fun toString(): String = "$name($code)"
}