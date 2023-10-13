package com.interrupt.server.member.api

import com.interrupt.server.common.api.BaseResponse
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckResponse
import com.interrupt.server.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController(
    private val memberService: MemberService
) {

    @GetMapping("/api/v1/members/check-login-id")
    fun checkLoginId(@Valid request: LoginIdDuplicateCheckRequest): BaseResponse<LoginIdDuplicateCheckResponse> =
        BaseResponse(message = "OK", data = memberService.checkLoginIdDuplication(request))

}