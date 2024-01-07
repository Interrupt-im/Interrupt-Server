package com.interrupt.server.auth.web

import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.auth.service.JwtService
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service

@Service
class CustomLogoutHandler(
    private val jwtService: JwtService,
    private val tokenRedisRepository: TokenRedisRepository
): LogoutHandler {

    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        val authHeader = request?.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return

        val jwt = authHeader.substring(7)
        val jti = jwtService.extractJti(jwt)

        tokenRedisRepository.findById(jti)?.let {
            tokenRedisRepository.deleteById(jti)
            SecurityContextHolder.clearContext()
        } ?: throw InterruptServerException(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED)
    }

}