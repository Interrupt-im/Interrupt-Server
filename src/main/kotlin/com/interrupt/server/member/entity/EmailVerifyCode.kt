package com.interrupt.server.member.entity

import com.interrupt.server.common.redis.RedisEntity

@RedisEntity
class EmailVerifyCode(
    val email: String,
    val verifyCode: String,
    var isVerified: Boolean = false
) {
    lateinit var uuid :String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmailVerifyCode) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }


}
