package com.interrupt.server.career.repository

import com.interrupt.server.career.entity.Career
import com.interrupt.server.member.entity.Member
import org.springframework.stereotype.Repository

@Repository
class CareerQueryRepository(
    private val careerRepository: CareerRepository,
) {

    fun findAllByMemberId(memberId: Long): List<Career> {
        return careerRepository.findAll {
            select(
                entity(Career::class)
            ).from(
                entity(Career::class),
                fetchJoin(Member::class).on(path(Member::id).equal(path(Career::member)(Member::id)))
            ).where(
                path(Career::member)(Member::id).equal(memberId)
            )
        }.filterNotNull()
    }

}