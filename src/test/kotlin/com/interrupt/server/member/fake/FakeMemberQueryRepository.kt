package com.interrupt.server.member.fake

import com.interrupt.server.member.application.MemberQueryRepository
import com.interrupt.server.member.domain.Member
import com.interrupt.server.member.fixture.MemberFixture

class FakeMemberQueryRepository : MemberQueryRepository {

    private val members = mutableMapOf<Long, Member>()

    override fun existsByEmailAndNotDeleted(email: String?): Boolean = members.values.any { it.email == email && it.deletedAt == null }

    override fun findByIdAndNotDeleted(id: Long): Member? = members[id]

    override fun findByEmailAndNotDeleted(email: String?): Member? = members.values.firstOrNull { it.email == email && it.deletedAt == null }

    fun init() {
        members.clear()

        listOf(MemberFixture.`고객 1`, MemberFixture.`고객 2`, MemberFixture.`삭제된 고객 1`)
            .map(MemberFixture::`회원 엔티티 생성`)
            .forEachIndexed { index, member ->
                members[index.toLong() + 1] = member
            }
    }
}
