package com.interrupt.server.member.dto.register

import com.interrupt.server.member.entity.Member

data class MemberRegisterResponse(
    var loginId: String,
    var name: String,
    var email: String
) {

    companion object {

        fun of(member: Member) = MemberRegisterResponse(loginId = member.loginId, name = member.name, email = member.email,)

    }

}