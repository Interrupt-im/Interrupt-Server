package com.interrupt.server.member.fake

import com.interrupt.server.member.application.MemberCommandRepository
import com.interrupt.server.member.application.MemberQueryRepository
import com.interrupt.server.member.domain.Member

class FakeMemberRepository : MemberQueryRepository, MemberCommandRepository {

    private val members = mutableMapOf<Long, Member>()

    override fun existsByEmailAndNotDeleted(email: String?): Boolean = members.values.any { it.email == email && it.deletedAt == null }

    override fun findByIdAndNotDeleted(id: Long): Member? = members[id]

    override fun findByEmailAndNotDeleted(email: String?): Member? = members.values.firstOrNull { it.email == email && it.deletedAt == null }

    fun init() {
        members.clear()
    }

    override fun save(member: Member): Member {
        this.members[member.id] = member

        return member
    }
}
