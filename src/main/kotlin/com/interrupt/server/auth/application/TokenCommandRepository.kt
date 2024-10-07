package com.interrupt.server.auth.application

import com.interrupt.server.auth.domain.Token

interface TokenCommandRepository {
    fun save(token: Token): Token

    fun deleteByJti(jti: String)
}
