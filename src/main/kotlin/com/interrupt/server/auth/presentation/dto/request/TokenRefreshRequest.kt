package com.interrupt.server.auth.presentation.dto.request

import com.interrupt.server.auth.application.command.TokenRefreshCommand

data class TokenRefreshRequest(
    val refreshToken: String?,
) {
    fun toCommand(username: String): TokenRefreshCommand = TokenRefreshCommand(username, refreshToken)
}
