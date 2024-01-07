package com.interrupt.server.auth.dto.response

data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String,
)
