package com.interrupt.server.member.dto.login

import com.interrupt.server.member.entity.Member

data class MemberLoginResponse(
    var loginId: String
) {

    companion object {
        fun of(foundMember: Member): MemberLoginResponse =
            MemberLoginResponse(
                loginId = foundMember.loginId
            )
    }

}
