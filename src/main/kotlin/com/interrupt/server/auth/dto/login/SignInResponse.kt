package com.interrupt.server.auth.dto.login

data class SignInResponse(
    val accessToken: String,
    val refreshToken: String
)