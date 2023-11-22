package com.interrupt.server.member.api

import com.interrupt.server.common.api.BaseResponse
import com.interrupt.server.member.dto.delete.MemberDeleteRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckResponse
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.dto.update.MemberUpdateRequest
import com.interrupt.server.member.service.MemberService
import com.interrupt.server.member.validation.annotation.loginid.LoginIdValidation
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
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
    fun updateMember(@PathVariable("loginId")
                     @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]+\$", message = "아이디는 영어(필수)와 숫자로 설정해야 합니다.")
                     @Size(min = 8, max = 20, message = "아이디는 8자 이상 20자 이하로 설정해야 합니다.")
                     @LoginIdValidation
                     loginId: String,
                     @RequestBody @Valid request: MemberUpdateRequest) =
        memberService.updateMember(loginId, request)

    @DeleteMapping("/api/v1/members/{loginId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMember(@PathVariable("loginId")
                     @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]+\$", message = "아이디는 영어(필수)와 숫자로 설정해야 합니다.")
                     @Size(min = 8, max = 20, message = "아이디는 8자 이상 20자 이하로 설정해야 합니다.")
                     @LoginIdValidation
                     loginId: String,
                     @RequestBody @Valid request: MemberDeleteRequest) =
        memberService.deleteMember(loginId, request)

}