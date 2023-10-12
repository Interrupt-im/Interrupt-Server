package com.interrupt.server.member.dto.register

import com.interrupt.server.member.entity.Member

data class MemberRegisterRequest(
    var loginId: String,
    var password: String,
    var name: String,
    var email: String,
    val emailVerifyCodeKey: String,
) {

    fun toEntity(): Member =
        Member(loginId = loginId, password = password, name = name, email = email,)

}