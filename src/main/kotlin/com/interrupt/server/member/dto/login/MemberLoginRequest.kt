package com.interrupt.server.member.dto.login

data class MemberLoginRequest(
    var loginId: String,
    var password: String,
) {

}
