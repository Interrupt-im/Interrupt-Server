package com.interrupt.server.member.application

import com.interrupt.server.member.domain.Member

interface MemberQueryRepository {
    fun existsByEmailAndNotDeleted(email: String?): Boolean
    fun findByIdAndNotDeleted(id: Long): Member?
    fun findByEmailAndNotDeleted(email: String?): Member?
}
