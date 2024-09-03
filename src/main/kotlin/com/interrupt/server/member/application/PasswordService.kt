package com.interrupt.server.member.application

import com.interrupt.server.member.domain.Password
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService(
    private val passwordEncoder: PasswordEncoder,
) {
    fun matches(rawPassword: String?, password: Password): Boolean = passwordEncoder.matches(rawPassword, password.password)
}
