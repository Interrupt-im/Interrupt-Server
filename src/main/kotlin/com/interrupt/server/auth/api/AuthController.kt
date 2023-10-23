package com.interrupt.server.auth.api

import com.interrupt.server.common.api.BaseResponse
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyRequest
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyResponse
import com.interrupt.server.member.dto.emailverify.EmailVerifyRequest
import com.interrupt.server.member.dto.login.MemberLoginRequest
import com.interrupt.server.member.dto.login.MemberLoginResponse
import com.interrupt.server.member.dto.recover.*
import com.interrupt.server.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class AuthController(
    private val memberService: MemberService,
) {

    @PostMapping("/api/v1/auth/verify-code")
    fun applySendEmailVerifyCode(@RequestBody @Valid request: EmailVerificationApplyRequest): BaseResponse<EmailVerificationApplyResponse> =
        BaseResponse(data = memberService.applySendEmailVerifyCode(request))

    @PostMapping("/api/v1/auth/verification/verify-code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun validateEmailVerifyCode(@RequestBody @Valid request: EmailVerifyRequest) =
        memberService.validateEmailVerifyCode(request)

    @PostMapping("/api/v1/auth/login")
    fun login(@RequestBody @Valid request: MemberLoginRequest): BaseResponse<MemberLoginResponse> =
        BaseResponse(data = memberService.login(request))

    @PostMapping("/api/v1/auth/recovery/login-id/verify-code")
    fun applySendLoginIdRecoverVerifyCode(@RequestBody @Valid request: RecoverLoginIdRequest): BaseResponse<RecoverLoginIdResponse> =
        BaseResponse(data = memberService.applySendLoginIdRecoverVerifyCode(request))

    @GetMapping("/api/v1/auth/recovery/login-id/verification")
    fun validateLoginIdRecoverVerifyCode(@Valid request: VerifyRecoverLoginIdRequest): BaseResponse<VerifyRecoverLoginIdResponse> =
        BaseResponse(data = memberService.validateLoginIdRecoverVerifyCode(request))

    @PostMapping("/api/v1/auth/recovery/password/verify-code")
    fun applySendPasswordRecoverVerifyCode(@RequestBody @Valid request: RecoverPasswordRequest): BaseResponse<RecoverPasswordResponse> =
        BaseResponse(data = memberService.applySendPasswordRecoverVerifyCode(request))

    @PutMapping("/api/v1/auth/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun validatePasswordRecoverVerifyCode(@RequestBody @Valid request: VerifyRecoverPasswordRequest) =
        memberService.validatePasswordRecoverVerifyCode(request)

}