package com.interrupt.server.global.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    // MEMBER
    BLANK_EMAIL(HttpStatus.BAD_REQUEST, "이메일 값이 없습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    BLANK_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호 값이 없습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 영어, 숫자, 특수문자로 이루어진 8자 이상 15자 이하여야 합니다."),
    BLANK_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임 값이 없습니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "닉네임은 한글, 영어, 숫자로 이루어진 8자 이상 15자 이하여야 합니다."),
    BLANK_MEMBER_TYPE(HttpStatus.BAD_REQUEST, "회원 유형 값이 없습니다."),
    DUPLICATED_REGISTER_EMAIL(HttpStatus.CONFLICT, "이미 존재 하는 이메일 입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 회원 정보를 찾을 수 없습니다."),

    // COMMON
    INTERNAL_SEVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의 바랍니다."),
}
