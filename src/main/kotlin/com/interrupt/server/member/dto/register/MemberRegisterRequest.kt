package com.interrupt.server.member.dto.register

import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.member.entity.Member

data class MemberRegisterRequest(
    val loginId: String,
    val password: String,
    val name: String,
    val email: String
) {

    fun toEntity(stringEncoder: StringEncoder): Member =
        Member(
            loginId = stringEncoder.encrypt(loginId),
            password = stringEncoder.encrypt(password),
            name = stringEncoder.encrypt(name),
            email = stringEncoder.encrypt(email),
        )

}