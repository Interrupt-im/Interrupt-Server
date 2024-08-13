package com.interrupt.server.member.infra

import com.interrupt.server.member.application.MemberCommandRepository
import com.interrupt.server.member.domain.Member
import org.springframework.stereotype.Repository

@Repository
class MemberCommandJdslRepository(
    private val memberJpaRepository : MemberJpaRepository,
) : MemberCommandRepository {
    override fun save(member: Member): Member = memberJpaRepository.save(member)
}
