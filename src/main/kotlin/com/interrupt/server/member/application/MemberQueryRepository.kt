package com.interrupt.server.member.application

interface MemberQueryRepository {
    fun existsByEmailAndNotDeleted(email: String?): Boolean
}
