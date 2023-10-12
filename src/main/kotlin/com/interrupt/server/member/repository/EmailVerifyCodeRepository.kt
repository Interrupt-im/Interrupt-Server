package com.interrupt.server.member.repository

import com.interrupt.server.member.entity.EmailVerifyCode
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.util.*

@Repository
class EmailVerifyCodeRepository(
    private val emailVerifyRedisTemplate: RedisTemplate<String, EmailVerifyCode>
) {

    companion object {
        private const val KEY_PREFIX = "EMAIL_VERIFY_CODE"
        private val EMAIL_VERIFY_CODE_TTL = Duration.ofMinutes(30)
    }

    fun save(emailVerifyCode: EmailVerifyCode): EmailVerifyCode {
        if (emailVerifyCode.isUuidInitialized()) updateInternal(emailVerifyCode)
        else saveInternal(emailVerifyCode)

        return emailVerifyCode
    }

    fun findByUuid(uuid: String): EmailVerifyCode? = emailVerifyRedisTemplate.opsForValue().get(generateKey(uuid))

    private fun saveInternal(emailVerifyCode: EmailVerifyCode) {
        val uuid = generateMemberRecoverKey()
        emailVerifyCode.uuid = uuid
        emailVerifyRedisTemplate.opsForValue().set(generateKey(uuid), emailVerifyCode, EMAIL_VERIFY_CODE_TTL)
    }

    private fun updateInternal(emailVerifyCode: EmailVerifyCode) {
        emailVerifyRedisTemplate.opsForValue().set(generateKey(emailVerifyCode.uuid), emailVerifyCode, EMAIL_VERIFY_CODE_TTL)
    }

    private fun generateMemberRecoverKey(): String = UUID.randomUUID().toString()

    private fun generateKey(uuid: String): String = "$KEY_PREFIX:$uuid"

}