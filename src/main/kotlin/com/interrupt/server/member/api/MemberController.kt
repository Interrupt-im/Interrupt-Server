package com.interrupt.server.member.api

import com.interrupt.server.common.api.BaseResponse
import com.interrupt.server.member.dto.delete.MemberDeleteRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckResponse
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.dto.update.MemberUpdateRequest
import com.interrupt.server.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class MemberController(
    private val memberService: MemberService
) {

    @GetMapping("/api/v1/members/check-login-id")
    fun checkLoginIdDuplication(@Valid request: LoginIdDuplicateCheckRequest): BaseResponse<LoginIdDuplicateCheckResponse> =
        BaseResponse(data = memberService.checkLoginIdDuplication(request))

    @PostMapping("/api/v1/members")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerMember(@RequestBody @Valid request: MemberRegisterRequest): BaseResponse<*> =
        BaseResponse(statusCode = HttpStatus.CREATED.value(), data = memberService.registerMember(request))

    @PatchMapping("/api/v1/members/{loginId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMember(@PathVariable("loginId") loginId: String, @RequestBody @Valid request: MemberUpdateRequest) =
        memberService.updateMember(loginId, request)

    @DeleteMapping("/api/v1/members/{loginId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMEmber(@PathVariable("loginId") loginId: String, @RequestBody @Valid request: MemberDeleteRequest) =
        memberService.deleteMember(loginId, request)

}