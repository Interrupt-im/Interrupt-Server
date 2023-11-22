package com.interrupt.server.member.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.interrupt.server.common.annotation.RedisEntity

@RedisEntity
@JsonIgnoreProperties(ignoreUnknown = true)
class MemberRecover(
    val email: String,
    val loginId: String,
    val verifyCode: String
) {
    lateinit var uuid: String

    fun isUuidInitialized(): Boolean = (this::uuid.isInitialized)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemberRecover) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }


}