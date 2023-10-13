package com.interrupt.server.member.dto.login

import com.interrupt.server.member.entity.Member

data class MemberLoginResponse(
    var name: String
) {

    companion object {
        fun of(foundMember: Member): MemberLoginResponse =
            MemberLoginResponse(
                name = foundMember.name
            )
    }

}
