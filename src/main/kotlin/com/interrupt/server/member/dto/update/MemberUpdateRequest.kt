package com.interrupt.server.member.dto.update

data class MemberUpdateRequest(
    val loginId: String,
    val password: String? = null,
    val name: String? = null,
    val email: String? = null,
    val emailVerifyCodeKey: String? = null
) {

}
