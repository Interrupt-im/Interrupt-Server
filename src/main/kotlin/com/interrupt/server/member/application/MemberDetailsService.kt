package com.interrupt.server.member.application

import com.interrupt.server.auth.application.UserDetailsService
import com.interrupt.server.auth.presentation.UserDetails
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode
import org.springframework.stereotype.Service

@Service
class MemberDetailsService(
    private val memberQueryRepository: MemberQueryRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        memberQueryRepository.findByEmailAndNotDeleted(username)?.let {
            return UserDetails(it.id, it.email)
        }

        throw ApplicationException(ErrorCode.MEMBER_NOT_FOUND)
    }
}
