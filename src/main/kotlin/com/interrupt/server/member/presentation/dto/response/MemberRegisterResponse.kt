package com.interrupt.server.member.presentation.dto.response

import com.interrupt.server.member.domain.Member
import com.interrupt.server.member.domain.MemberType

class MemberRegisterResponse(member: Member) {
    val id: Long = member.id
    val email: String = member.email
    val nickname: String = member.nickname
    val memberType: MemberType = member.memberType
}
