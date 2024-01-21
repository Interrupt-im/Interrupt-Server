package com.interrupt.server.auth.web

import com.interrupt.server.auth.entity.AuthenticationCredentials
import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.auth.service.JwtService
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import jakarta.servlet.http.HttpServletRequest
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder

class CustomLogoutHandlerTest {

    private val jwtService: JwtService = mockk()
    private val tokenRepository: TokenRedisRepository = mockk()

    private val logoutHandler: CustomLogoutHandler = CustomLogoutHandler(jwtService, tokenRepository)

    @Test
    fun `로그아웃 요청을 받아 토큰을 삭제한다`() {
        // given
        val request: HttpServletRequest = mockk()
        val jwt = "token"
        val jti = "jti"
        val authenticationCredentials: AuthenticationCredentials = mockk()

        every { request.getHeader("Authorization") } returns "Bearer $jwt"
        every { jwtService.getJti(jwt) } returns jti
        every { tokenRepository.findById(jti) } returns authenticationCredentials
        every { tokenRepository.deleteById(jti) } returns true
        mockkStatic(SecurityContextHolder::class)
        justRun { SecurityContextHolder.clearContext() }

        // when then
        logoutHandler.logout(request, mockk(), mockk())
    }

    @Test
    fun `로그아웃 요청에 포함된 토큰으로 토큰 저장소에서 토큰을 찾을 수 없을 때 예외를 반환한다`() {
        // given
        val request: HttpServletRequest = mockk()
        val jwt = "token"
        val jti = "jti"

        every { request.getHeader("Authorization") } returns "Bearer $jwt"
        every { jwtService.getJti(jwt) } returns jti
        every { tokenRepository.findById(jti) } returns null

        // when then
        assertThatThrownBy { logoutHandler.logout(request, mockk(), mockk()) }
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED.message)
    }

}