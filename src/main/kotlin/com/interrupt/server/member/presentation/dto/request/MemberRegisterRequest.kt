package com.interrupt.server.member.presentation.dto.request

import com.interrupt.server.member.application.command.MemberCreateCommand
import com.interrupt.server.member.domain.MemberType


data class MemberRegisterRequest(
    val email: String?,
    val loginPassword: String?,
    val nickname: String?,
    val memberType: MemberType?
) {
    fun toCommand(): MemberCreateCommand = MemberCreateCommand(email, loginPassword, nickname, memberType)
}
