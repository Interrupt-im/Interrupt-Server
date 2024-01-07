package com.interrupt.server.auth.dto.request

data class TokenRefreshRequest(
    val refreshToken: String,
)