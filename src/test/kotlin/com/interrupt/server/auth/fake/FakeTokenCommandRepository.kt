package com.interrupt.server.auth.fake

import com.interrupt.server.auth.application.TokenCommandRepository
import com.interrupt.server.auth.domain.Token

class FakeTokenCommandRepository : TokenCommandRepository {

    private val tokens = mutableListOf<Token>()

    override fun save(token: Token): Token {
        tokens.add(token)
        return token
    }

    override fun deleteByJti(jti: String) {
        tokens.removeIf { it.jti == jti }
    }

    fun init() {
        tokens.clear()
    }
}
