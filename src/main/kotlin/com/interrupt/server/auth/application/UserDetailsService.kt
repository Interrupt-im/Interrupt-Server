package com.interrupt.server.auth.application

import com.interrupt.server.auth.presentation.UserDetails

interface UserDetailsService {
    fun loadUserByUsername(username: String): UserDetails?
}
