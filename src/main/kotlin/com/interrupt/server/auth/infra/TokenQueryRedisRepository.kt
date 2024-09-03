package com.interrupt.server.auth.infra

import com.fasterxml.jackson.databind.ObjectMapper
import com.interrupt.server.auth.application.TokenQueryRepository
import com.interrupt.server.auth.domain.Token
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository

@Repository
class TokenQueryRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : TokenQueryRepository {

    private val valueOperations: ValueOperations<String, String>
        get() = this.redisTemplate.opsForValue()

    override fun findByJti(jti: String): Token? = valueOperations[jti]?.let { objectMapper.readValue(it, Token::class.java) } ?: run { null }
}
