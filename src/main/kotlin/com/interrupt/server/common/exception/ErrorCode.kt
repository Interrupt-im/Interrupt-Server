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
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 회원 정보를 찾을 수 없습니다.", "ME0006"),

    // CAREER
    INVALID_ONGOING_CAREER_NUMBER(HttpStatus.BAD_REQUEST, "현재 진행중인 경력은 한개 이하여야 합니다.", "CE0001"),
    INVALID_ONGOING_CAREER_INDEX(HttpStatus.BAD_REQUEST, "현재 진행중인 경력의 날짜는 가장 마지막 순서여야 합니다.", "CE0002"),
    INVALID_CAREER_PERIOD(HttpStatus.BAD_REQUEST, "경력 입력이 올바르지 않습니다. 시작일과 종료일을 올바르게 입력하여 주세요.", "CE0003"),
    OVERLAPPED_CAREER_PERIOD(HttpStatus.BAD_REQUEST, "기간이 겹치는 경력이 존재합니다.", "CE0004"),

    // INPUT VALIDATION
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다.", "VE0001"),
    NO_CONTENT_HTTP_BODY(HttpStatus.BAD_REQUEST, "정상적인 요청 본문이 아닙니다.", "VE0002"),

    // REQUEST ERROR
    NOT_SUPPORTED_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "정상적인 요청이 아닙니다.", "RE0001"),

    // COMMON
    INTERNAL_SEVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "관리자에게 문의 바랍니다.", "IE0001"),
    ;

    override fun toString(): String = "$name($code)"
}