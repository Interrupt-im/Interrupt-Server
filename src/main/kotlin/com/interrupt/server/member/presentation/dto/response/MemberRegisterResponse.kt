package com.interrupt.server.member.presentation.dto.response

import com.interrupt.server.member.domain.Member
import com.interrupt.server.member.domain.MemberType

data class MemberRegisterResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val memberType: MemberType,
) {
    constructor(member: Member): this(
        member.id,
        member.email,
        member.nickname,
        member.memberType,
    )
}
