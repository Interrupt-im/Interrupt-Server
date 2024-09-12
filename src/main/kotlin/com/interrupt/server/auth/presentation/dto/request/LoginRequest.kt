package com.interrupt.server.auth.presentation.dto.request

import com.interrupt.server.auth.application.command.LoginCommand

data class LoginRequest(
    val email: String?,
    val password: String?
) {
    fun toCommand(): LoginCommand = LoginCommand(email, password)
}
