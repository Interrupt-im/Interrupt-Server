package com.interrupt.server.member.repository

import com.interrupt.server.member.entity.MemberRecover
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.util.UUID

@Repository
class MemberRecoverRepository(
    private val memberRecoverRedisTemplate: RedisTemplate<String, MemberRecover>,
) {

    companion object {
        private const val KEY_PREFIX = "MEMBER_RECOVER"
        private val MEMBER_RECOVER_TTL = Duration.ofMinutes(30)
    }

    fun save(memberRecover: MemberRecover): MemberRecover {
        val uuid = generateMemberRecoverKey()
        memberRecover.uuid = uuid
        memberRecoverRedisTemplate.opsForValue().set(generateKey(uuid), memberRecover, MEMBER_RECOVER_TTL)

        return memberRecover
    }

    fun findByUuid(uuid: String): MemberRecover? = memberRecoverRedisTemplate.opsForValue().get(generateKey(uuid))

    private fun generateMemberRecoverKey(): String = UUID.randomUUID().toString()

    private fun generateKey(uuid: String): String = "$KEY_PREFIX:$uuid"


}