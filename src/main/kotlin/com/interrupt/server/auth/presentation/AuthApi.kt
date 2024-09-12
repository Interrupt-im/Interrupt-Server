package com.interrupt.server.auth.presentation

import com.interrupt.server.auth.application.AuthCommandService
import com.interrupt.server.auth.application.command.LogoutCommand
import com.interrupt.server.auth.presentation.dto.request.LoginRequest
import com.interrupt.server.auth.presentation.dto.response.LoginResponse
import com.interrupt.server.global.common.SuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthApi(
    private val authCommandService: AuthCommandService
) {

    @PostMapping("/api/auth/login")
    fun login(@RequestBody request: LoginRequest): SuccessResponse<LoginResponse> = SuccessResponse(LoginResponse(authCommandService.login(request.toCommand())))

    @PostMapping("/api/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@LoginUser userDetails: UserDetails) = authCommandService.logout(LogoutCommand(userDetails.token.jti))
}
