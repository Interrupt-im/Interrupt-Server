package com.interrupt.server.auth.presentation

import com.interrupt.server.auth.domain.AuthenticationCredentials
import java.security.Principal

class UserDetails(
    val id: Long,
    val username: String,
) : Principal {

    lateinit var token: AuthenticationCredentials
        private set

    override fun getName(): String = username

    fun setToken(token: AuthenticationCredentials) {
        if (!this::token.isInitialized) {
            this.token = token
        }
    }
}
