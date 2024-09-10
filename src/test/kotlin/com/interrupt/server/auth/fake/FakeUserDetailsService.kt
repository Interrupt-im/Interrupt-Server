package com.interrupt.server.auth.fake

import com.interrupt.server.auth.application.UserDetailsService
import com.interrupt.server.auth.presentation.UserDetails
import com.interrupt.server.member.fake.FakeMemberQueryRepository

class FakeUserDetailsService(
    private val memberQueryRepository : FakeMemberQueryRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        memberQueryRepository.findByEmailAndNotDeleted(username)?.let {
            return UserDetails(it.id, it.email)
        }

        return null
    }
}
