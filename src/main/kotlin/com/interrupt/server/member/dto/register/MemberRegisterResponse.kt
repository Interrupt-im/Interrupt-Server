package com.interrupt.server.member.dto.register

import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.member.entity.Member

data class MemberRegisterResponse(
    val loginId: String,
    val name: String,
    val email: String
) {

    companion object {

        fun of(member: Member, stringEncoder: StringEncoder) = MemberRegisterResponse(
            loginId = stringEncoder.decrypt(member.loginId),
            name = stringEncoder.decrypt(member.name),
            email = stringEncoder.decrypt(member.email),
        )

    }

}