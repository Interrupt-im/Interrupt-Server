package com.interrupt.server.auth.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.interrupt.server.auth.entity.AuthenticationCredentials
import com.interrupt.server.auth.entity.TokenCache
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class TokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val valueOperations: ValueOperations<String, String>
        get() = this.redisTemplate.opsForValue()

    fun save(tokenCache: TokenCache) {
        valueOperations[tokenCache.key] = objectMapper.writeValueAsString(tokenCache.value)

        valueOperations.getAndExpire(tokenCache.key, tokenCache.ttl, TimeUnit.MILLISECONDS)
    }

    fun findById(id: String): AuthenticationCredentials? =
        valueOperations[id]?.let { objectMapper.readValue(it, AuthenticationCredentials::class.java) } ?: run { null }

    fun deleteById(id: String) = redisTemplate.delete(id)

    fun deleteAll() = redisTemplate.delete(redisTemplate.keys("*"))
}