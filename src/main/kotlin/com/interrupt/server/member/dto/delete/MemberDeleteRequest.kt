package com.interrupt.server.member.dto.delete

data class MemberDeleteRequest(
    var loginId: String,
    var password: String
) {

}
