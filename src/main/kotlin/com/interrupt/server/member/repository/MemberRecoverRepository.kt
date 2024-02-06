package com.interrupt.server.member.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.interrupt.server.member.entity.MemberRecover
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit

@Repository
class MemberRecoverRepository(
    private val memberRecoverRedisTemplate: RedisTemplate<String, String>,
) {

    companion object {
        private const val KEY_PREFIX = "MEMBER_RECOVER"
        private val MEMBER_RECOVER_TTL = Duration.ofMinutes(30)
    }

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val valueOperations: ValueOperations<String, String>
        get() = this.memberRecoverRedisTemplate.opsForValue()

    fun save(memberRecover: MemberRecover): MemberRecover {
        if (memberRecover.isUuidInitialized()) updateInternal(memberRecover)
        else saveInternal(memberRecover)

        return memberRecover
    }

    fun findByUuid(uuid: String): MemberRecover? =
        valueOperations.getAndExpire(generateKey(uuid), MEMBER_RECOVER_TTL)?.let { objectMapper.readValue(it, MemberRecover::class.java) } ?: run { null }

    private fun saveInternal(memberRecover: MemberRecover) {
        memberRecover.uuid = generateMemberRecoverKey()
        val key = generateKey(memberRecover.uuid)

        valueOperations[key] = objectMapper.writeValueAsString(memberRecover)
        valueOperations.getAndExpire(key, MEMBER_RECOVER_TTL)
    }

    private fun updateInternal(memberRecover: MemberRecover) {
        val key = generateKey(memberRecover.uuid)

        valueOperations[key] = objectMapper.writeValueAsString(memberRecover)
        valueOperations.getAndExpire(key, MEMBER_RECOVER_TTL)
    }

    private fun generateMemberRecoverKey(): String = UUID.randomUUID().toString()

    private fun generateKey(uuid: String): String = "$KEY_PREFIX:$uuid"


}