package com.interrupt.server.member.dto

import com.interrupt.server.member.entity.Member

data class MemberDto(
    val id: Long? = null,
    val loginId: String,
    val password: String,
    val name: String,
    val email: String
) {

    fun toEntity(): Member = Member(loginId, password, name, email)

    companion object {
        fun of(entity: Member) = entity.let { MemberDto(it.id, it.loginId, it.password, it.name, it.email) }
    }

}
