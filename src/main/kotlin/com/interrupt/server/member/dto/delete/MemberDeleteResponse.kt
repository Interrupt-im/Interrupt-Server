package com.interrupt.server.member.dto.delete

import com.interrupt.server.member.entity.Member

data class MemberDeleteResponse(
    var loginId: String
) {
    companion object {
        fun of(savedMember: Member): MemberDeleteResponse =
            MemberDeleteResponse(savedMember.loginId)
    }

}
