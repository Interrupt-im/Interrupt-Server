package com.interrupt.server.auth.dto.refresh

data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String,
)
