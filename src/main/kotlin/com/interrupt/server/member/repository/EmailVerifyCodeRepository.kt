package com.interrupt.server.member.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.interrupt.server.member.entity.EmailVerifyCode
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository
import java.time.Duration
import java.util.*

@Repository
class EmailVerifyCodeRepository(
    private val emailVerifyRedisTemplate: RedisTemplate<String, String>
) {

    companion object {
        private const val KEY_PREFIX = "EMAIL_VERIFY_CODE"
        private val EMAIL_VERIFY_CODE_TTL = Duration.ofMinutes(30)
    }

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val valueOperations: ValueOperations<String, String>
        get() = this.emailVerifyRedisTemplate.opsForValue()

    fun save(emailVerifyCode: EmailVerifyCode): EmailVerifyCode {
        if (emailVerifyCode.isUuidInitialized()) updateInternal(emailVerifyCode)
        else saveInternal(emailVerifyCode)

        return emailVerifyCode
    }

    fun findByUuid(uuid: String): EmailVerifyCode? =
        valueOperations.getAndExpire(generateKey(uuid), EMAIL_VERIFY_CODE_TTL)?.let { objectMapper.readValue(it, EmailVerifyCode::class.java) } ?: run { null }

    private fun saveInternal(emailVerifyCode: EmailVerifyCode) {
        emailVerifyCode.uuid = generateEmailVerifyCodeKey()
        val key = generateKey(emailVerifyCode.uuid)

        valueOperations[key] = objectMapper.writeValueAsString(emailVerifyCode)
        valueOperations.getAndExpire(key, EMAIL_VERIFY_CODE_TTL)
    }

    private fun updateInternal(emailVerifyCode: EmailVerifyCode) {
        val key = generateKey(emailVerifyCode.uuid)

        valueOperations[key] = objectMapper.writeValueAsString(emailVerifyCode)
        valueOperations.getAndExpire(key, EMAIL_VERIFY_CODE_TTL)
    }

    private fun generateEmailVerifyCodeKey(): String = UUID.randomUUID().toString()

    private fun generateKey(uuid: String): String = "$KEY_PREFIX:$uuid"

}