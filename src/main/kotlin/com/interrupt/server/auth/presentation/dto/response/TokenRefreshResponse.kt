package com.interrupt.server.auth.presentation.dto.response

import com.interrupt.server.auth.domain.AuthenticationCredentials

data class TokenRefreshResponse(
    val refreshToken: String,
) {
    constructor(authenticationCredentials: AuthenticationCredentials) : this(authenticationCredentials.refreshToken)
}
