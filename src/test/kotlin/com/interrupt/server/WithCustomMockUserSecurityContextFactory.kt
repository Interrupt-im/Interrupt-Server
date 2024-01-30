package com.interrupt.server

import com.interrupt.WithCustomMockUser
import com.interrupt.server.member.entity.Member
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithCustomMockUserSecurityContextFactory: WithSecurityContextFactory<WithCustomMockUser> {
    override fun createSecurityContext(annotation: WithCustomMockUser): SecurityContext {
        val member = Member(annotation.loginId, annotation.password, annotation.name, annotation.email, annotation.role)

        return SecurityContextHolder.getContext().apply {
            authentication = UsernamePasswordAuthenticationToken(member, member.password, listOf(SimpleGrantedAuthority(member.role.name)))
        }
    }
}