package com.interrupt.server.auth.dto.refresh

data class TokenRefreshRequest(
    val refreshToken: String,
)