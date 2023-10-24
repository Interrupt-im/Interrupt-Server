package com.interrupt.server.auth.api

import com.interrupt.server.common.api.BaseResponse
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyRequest
import com.interrupt.server.member.dto.emailverify.EmailVerificationApplyResponse
import com.interrupt.server.member.dto.emailverify.EmailVerifyRequest
import com.interrupt.server.member.dto.login.MemberLoginRequest
import com.interrupt.server.member.dto.login.MemberLoginResponse
import com.interrupt.server.member.dto.recover.*
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

    @PostMapping("/api/v1/auth/login-id/recovery/verify-code")
    fun applySendLoginIdRecoverVerifyCode(@RequestBody @Valid request: RecoverLoginIdRequest): BaseResponse<RecoverLoginIdResponse> =
        BaseResponse(data = memberService.applySendLoginIdRecoverVerifyCode(request))

    @GetMapping("/api/v1/auth/login-id")
    fun validateLoginIdRecoverVerifyCode(@Valid request: VerifyRecoverLoginIdRequest): BaseResponse<VerifyRecoverLoginIdResponse> =
        BaseResponse(data = memberService.validateLoginIdRecoverVerifyCode(request))

    @PostMapping("/api/v1/auth/password/recovery/verify-code")
    fun applySendPasswordRecoverVerifyCode(@RequestBody @Valid request: RecoverPasswordRequest): BaseResponse<RecoverPasswordResponse> =
        BaseResponse(data = memberService.applySendPasswordRecoverVerifyCode(request))

    @PutMapping("/api/v1/auth/member/{loginId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun validatePasswordRecoverVerifyCode(@PathVariable("loginId")
                                          @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]+\$", message = "아이디는 영어(필수)와 숫자로 설정해야 합니다.")
                                          @Size(min = 8, max = 20, message = "아이디는 8자 이상 20자 이하로 설정해야 합니다.")
                                          @LoginIdValidation
                                          loginId: String,
                                          @RequestBody @Valid request: VerifyRecoverPasswordRequest) =
        memberService.validatePasswordRecoverVerifyCode(request)

}