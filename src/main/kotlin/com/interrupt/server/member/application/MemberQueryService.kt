package com.interrupt.server.member.application

import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode
import com.interrupt.server.member.domain.Member
import org.springframework.stereotype.Service

@Service
class MemberQueryService(
    private val memberQueryRepository : MemberQueryRepository
) {
    fun findById(id: Long): Member =
        memberQueryRepository.findByIdAndNotDeleted(id) ?: throw ApplicationException(ErrorCode.MEMBER_NOT_FOUND)
}
