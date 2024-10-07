package com.interrupt.server.auth.application

import com.interrupt.server.auth.domain.Token

interface TokenQueryRepository {
    fun findByJti(jti: String): Token?
}
