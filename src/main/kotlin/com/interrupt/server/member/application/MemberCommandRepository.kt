package com.interrupt.server.member.application

import com.interrupt.server.member.domain.Member

interface MemberCommandRepository {
    fun save(member: Member): Member
}
