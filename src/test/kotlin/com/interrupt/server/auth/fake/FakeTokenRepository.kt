package com.interrupt.server.auth.fake

import com.interrupt.server.auth.application.TokenCommandRepository
import com.interrupt.server.auth.application.TokenQueryRepository
import com.interrupt.server.auth.domain.Token
import com.interrupt.server.auth.fixture.TokenFixture

class FakeTokenRepository : TokenCommandRepository, TokenQueryRepository {

    private val tokens = mutableMapOf<String, Token>()

    override fun save(token: Token): Token {
        tokens[token.jti] = token
        return token
    }

    override fun deleteByJti(jti: String) {
        tokens.remove(jti)
    }

    override fun findByJti(jti: String): Token? = tokens[jti]

    fun init() {
        tokens.clear()

        TokenFixture.entries
            .filter { it.jti != null }
            .map(TokenFixture::`토큰 엔티티 생성`)
            .forEach { token ->
                tokens[token.jti] = token
            }
    }
}
