package com.interrupt.server.member.fixture

import com.interrupt.server.member.domain.Member

enum class MemberFixture(
    val email: String?,
    val loginPassword: String?,
    val nickname: String?,
    val memberType: String?,
) {
    // 정상 객체
    `고객 1`("member1@domain.com", "password123", "nickname", "CUSTOMER"),

    // 비정상 객체
    `이메일 NULL 회원`(null, "password123", "nickname", "CUSTOMER"),
    `이메일 공백 회원`("", "password123", "nickname", "CUSTOMER"),
    `이메일 형식 비정상 회원1`("memberdomail.com", "password123", "nickname", "CUSTOMER"),
    `이메일 형식 비정상 회원2`("member@domain", "password123", "nickname", "CUSTOMER"),
    `이메일 형식 비정상 회원3`("member", "password123", "nickname", "CUSTOMER"),
    `이메일 형식 비정상 회원4`("@domain.com", "password123", "nickname", "CUSTOMER"),
    `이메일 형식 비정상 회원5`("member.com", "password123", "nickname", "CUSTOMER"),
    `이메일 형식 비정상 회원6`("member@.com", "password123", "nickname", "CUSTOMER"),
    `비밀번호 NULL 회원`("member@domain.com", null, "nickname", "CUSTOMER"),
    `비밀번호 공백 회원`("member@domain.com", "", "nickname", "CUSTOMER"),
    `비밀번호 형식 비정상 회원1`("member@domain.com", "pass123", "nickname", "CUSTOMER"),
    `비밀번호 형식 비정상 회원2`("member@domain.com", "longlonglong1234", "nickname", "CUSTOMER"),
    `비밀번호 형식 비정상 회원3`("member@domain.com", "한국어비밀번호1", "nickname", "CUSTOMER"),
    `비밀번호 형식 비정상 회원4`("member@domain.com", "!@#$%^&*", "nickname", "CUSTOMER"),
    `비밀번호 형식 비정상 회원5`("member@domain.com", "!@#$%^&*1", "nickname", "CUSTOMER"),
    `닉네임 NULL 회원`("member1@domain.com", "password123", null, "CUSTOMER"),
    `닉네임 공백 회원`("member1@domain.com", "password123", "", "CUSTOMER"),
    `닉네임 형식 비정상 회원1`("member1@domain.com", "password123", "nicknam", "CUSTOMER"),
    `닉네임 형식 비정상 회원2`("member1@domain.com", "password123", "longnickname1234", "CUSTOMER"),
    `닉네임 형식 비정상 회원3`("member1@domain.com", "password123", "nickname!", "CUSTOMER"),
    `회원 유형 NULL 회원`("member1@domain.com", "password123", "nickname", null),
    `회원 유형 공백 회원`("member1@domain.com", "password123", "nickname", ""),
    `없는 회원 유형 회원`("member1@domain.com", "password123", "nickname", "FOO"),
    ;

    fun `회원 엔티티 생성`(): Member = Member(email!!, loginPassword!!, nickname!!, memberType!!)
}
