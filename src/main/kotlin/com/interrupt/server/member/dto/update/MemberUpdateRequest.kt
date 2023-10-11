package com.interrupt.server.member.dto.update

data class MemberUpdateRequest(
    var loginId: String,
    var password: String? = null,
    var name: String? = null,
    var email: String? = null,
) {

}
