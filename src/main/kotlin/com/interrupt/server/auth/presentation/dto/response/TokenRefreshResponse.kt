package com.interrupt.server.auth.presentation.dto.response

import com.interrupt.server.auth.domain.AuthenticationCredentials

data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String,
) {
    constructor(authenticationCredentials: AuthenticationCredentials) : this(authenticationCredentials.accessToken, authenticationCredentials.refreshToken)
}
