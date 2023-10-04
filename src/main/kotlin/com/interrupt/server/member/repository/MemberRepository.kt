package com.interrupt.server.member.repository

import com.interrupt.server.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository: JpaRepository<Member, Long> {

    fun findByLoginId(loginId: String): Member?

}