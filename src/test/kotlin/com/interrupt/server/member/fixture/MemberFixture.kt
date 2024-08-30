package com.interrupt.server.member.fixture

import com.interrupt.server.member.application.command.MemberCreateCommand
import com.interrupt.server.member.domain.Member
import com.interrupt.server.member.domain.MemberType
import com.interrupt.server.member.domain.Password
import com.interrupt.server.member.presentation.dto.request.MemberRegisterRequest
import org.springframework.security.crypto.password.PasswordEncoder

enum class MemberFixture(
    val email: String?,
    val password: String?,
    val nickname: String?,
    val memberType: MemberType?,
) {
    // 정상 객체
    `고객 1`("member1@domain.com", "password123", "nickname", MemberType.CUSTOMER),

    // 비정상 객체
    `이메일 NULL 회원`(null, "password123", "nickname", MemberType.CUSTOMER),
    `이메일 공백 회원`("", "password123", "nickname", MemberType.CUSTOMER),
    `이메일 형식 비정상 회원1`("memberdomail.com", "password123", "nickname", MemberType.CUSTOMER),
    `이메일 형식 비정상 회원2`("member@domain", "password123", "nickname", MemberType.CUSTOMER),
    `이메일 형식 비정상 회원3`("member", "password123", "nickname", MemberType.CUSTOMER),
    `이메일 형식 비정상 회원4`("@domain.com", "password123", "nickname", MemberType.CUSTOMER),
    `이메일 형식 비정상 회원5`("member.com", "password123", "nickname", MemberType.CUSTOMER),
    `이메일 형식 비정상 회원6`("member@.com", "password123", "nickname", MemberType.CUSTOMER),
    `비밀번호 NULL 회원`("member@domain.com", null, "nickname", MemberType.CUSTOMER),
    `비밀번호 공백 회원`("member@domain.com", "", "nickname", MemberType.CUSTOMER),
    `비밀번호 형식 비정상 회원1`("member@domain.com", "pass123", "nickname", MemberType.CUSTOMER),
    `비밀번호 형식 비정상 회원2`("member@domain.com", "longlonglong1234", "nickname", MemberType.CUSTOMER),
    `비밀번호 형식 비정상 회원3`("member@domain.com", "한국어비밀번호1", "nickname", MemberType.CUSTOMER),
    `비밀번호 형식 비정상 회원4`("member@domain.com", "!@#$%^&*", "nickname", MemberType.CUSTOMER),
    `비밀번호 형식 비정상 회원5`("member@domain.com", "!@#$%^&*1", "nickname", MemberType.CUSTOMER),
    `닉네임 NULL 회원`("member1@domain.com", "password123", null, MemberType.CUSTOMER),
    `닉네임 공백 회원`("member1@domain.com", "password123", "", MemberType.CUSTOMER),
    `닉네임 형식 비정상 회원1`("member1@domain.com", "password123", "nicknam", MemberType.CUSTOMER),
    `닉네임 형식 비정상 회원2`("member1@domain.com", "password123", "longnickname1234", MemberType.CUSTOMER),
    `닉네임 형식 비정상 회원3`("member1@domain.com", "password123", "nickname!", MemberType.CUSTOMER),
    `회원 유형 NULL 회원`("member1@domain.com", "password123", "nickname", null),
    ;

    fun `회원 엔티티 생성`(passwordEncoder: PasswordEncoder): Member =
        Member(email, Password(password, passwordEncoder), nickname, memberType)
    fun `회원 생성 COMMAND 생성`(): MemberCreateCommand =
        MemberCreateCommand(email, password, nickname, memberType)
    fun `회원 가입 요청 DTO 생성`(): MemberRegisterRequest = MemberRegisterRequest(email, password, nickname, memberType)
}
