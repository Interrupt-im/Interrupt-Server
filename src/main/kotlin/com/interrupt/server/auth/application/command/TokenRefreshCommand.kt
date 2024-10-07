package com.interrupt.server.auth.application.command

import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode

data class TokenRefreshCommand(
    val email: String,
    val refreshToken: String?,
) {
    init {
        require (refreshToken != null) {
            throw ApplicationException(ErrorCode.EMPTY_PARAM)
        }
    }
}
