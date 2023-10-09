package com.interrupt.server.member.dto.update

data class MemberUpdateRequest(
    var originalLoginId: String,
    var loginId: String? = null,
    var password: String? = null,
    var name: String? = null,
    var email: String? = null,
) {

}
