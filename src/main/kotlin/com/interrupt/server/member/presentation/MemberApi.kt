package com.interrupt.server.member.presentation

import com.interrupt.server.global.common.SuccessResponse
import com.interrupt.server.member.application.MemberCommandService
import com.interrupt.server.member.application.MemberQueryService
import com.interrupt.server.member.presentation.dto.request.MemberRegisterRequest
import com.interrupt.server.member.presentation.dto.response.MemberRegisterResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberApi(
    private val memberCommandService: MemberCommandService,
    private val memberQueryService: MemberQueryService
) {

    @PostMapping("/api/members")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: MemberRegisterRequest): SuccessResponse<MemberRegisterResponse> {
        val id = memberCommandService.createMember(request.toCommand())
        return SuccessResponse(MemberRegisterResponse(memberQueryService.findById(id)), HttpStatus.CREATED)
    }
}
