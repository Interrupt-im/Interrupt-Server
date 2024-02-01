package com.interrupt.server.member.repository

import com.interrupt.server.member.entity.Member
import org.springframework.stereotype.Repository

@Repository
class MemberQueryRepository(
    private val memberRepository: MemberRepository
) {

    fun findByLoginId(loginId: String): Member? = memberRepository.findAll {
        select(
            entity(Member::class)
        ).from(
            entity(Member::class)
        ).where(
            path(Member::deletedAt).isNull()
                .and(
                    path(Member::loginId).equal(loginId)
                )
        )
    }.firstOrNull()

    fun findByNameAndEmail(name: String, email: String): Member? = memberRepository.findAll {
        select(
            entity(Member::class)
        ).from(
            entity(Member::class)
        ).where(
            path(Member::deletedAt).isNull()
                .and(
                    path(Member::name).equal(name)
                ).and(
                    path(Member::email).equal(email)
                )
        )
    }.firstOrNull()

    fun findByLoginIdAndEmail(loginId: String, email: String): Member? = memberRepository.findAll {
        select(
            entity(Member::class)
        ).from(
            entity(Member::class)
        ).where(
            path(Member::deletedAt).isNull()
                .and(
                    path(Member::loginId).equal(loginId)
                ).and(
                    path(Member::email).equal(email)
                )
        )
    }.firstOrNull()

}