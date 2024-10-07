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

    // AUTH
    NOT_MATCH_LOGIN_FIELD(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 잘못 입력 하셨습니다."),
    EMPTY_CLAIM(HttpStatus.UNAUTHORIZED, "CLAIM 정보가 비어있습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "액세스 토큰이 유효하지 않습니다."),
    MISS_MATCH_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 일치하지 않습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
    INVALID_TOKEN_REISSUE_REQUEST(HttpStatus.BAD_REQUEST, "토큰을 재발급 할 수 없습니다."),
    UNAUTHORIZED_ACCESS_ATTEMPT(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),

    // COMMON
    INTERNAL_SEVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의 바랍니다."),
    NO_CONTENT_HTTP_BODY(HttpStatus.BAD_REQUEST, "정상적인 요청 본문이 아닙니다."),
    NOT_SUPPORTED_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "정상적인 요청이 아닙니다."),
    EMPTY_PARAM(HttpStatus.BAD_REQUEST, "파라미터가 없습니다."),
}
