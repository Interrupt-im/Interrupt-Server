package com.interrupt.server.member.dto.login

data class SignInResponse(
    val accessToken: String,
    val refreshToken: String
)