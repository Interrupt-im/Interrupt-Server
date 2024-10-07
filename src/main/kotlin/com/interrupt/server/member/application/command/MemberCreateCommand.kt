package com.interrupt.server.member.application.command

import com.interrupt.server.member.domain.Member
import com.interrupt.server.member.domain.MemberType
import com.interrupt.server.member.domain.Password
import org.springframework.security.crypto.password.PasswordEncoder

data class MemberCreateCommand(
    val email: String?,
    val password: String?,
    val nickname: String?,
    val memberType: MemberType?,
) {
    fun toEntity(passwordEncoder: PasswordEncoder): Member = Member(email, Password(password, passwordEncoder), nickname, memberType)
}
