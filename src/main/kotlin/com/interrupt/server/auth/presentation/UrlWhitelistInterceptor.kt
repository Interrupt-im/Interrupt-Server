package com.interrupt.server.auth.presentation

import com.interrupt.server.auth.application.JwtService
import com.interrupt.server.auth.application.TokenQueryRepository
import com.interrupt.server.auth.application.WhitelistUrl
import com.interrupt.server.auth.domain.Token
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

class UrlWhitelistInterceptor(
    private val jwtService: JwtService,
    private val tokenQueryRepository: TokenQueryRepository,
    private vararg val whitelistUrls: WhitelistUrl = arrayOf()
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val uri = request.requestURI
        val method = request.method

        if (whitelistUrls.any { it.match(uri, method) }) {
            return true
        }

        val authorization = request.getHeader(AUTH_HEADER_NAME)

        checkAuthHeader(authorization)

        val token = authorization.substring(AUTH_HEADER_PREFIX.length)
        val username = jwtService.getUsername(token)
        val jti = jwtService.getJti(token)
        val savedToken = tokenQueryRepository.findByJti(jti)

        checkValidAccessToken(token, savedToken)

        request.setAttribute(USERNAME_ATTRIBUTE_KEY, username)
        request.setAttribute(TOKEN_ATTRIBUTE_KEY, savedToken)

        return true
    }

    private fun checkAuthHeader(authorization: String?) {
        check(!authorization.isNullOrBlank() && authorization.startsWith(AUTH_HEADER_PREFIX)) {
            throw ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS_ATTEMPT)
        }
    }

    private fun checkValidAccessToken(token: String, savedToken: Token?) {
        check(savedToken != null && savedToken.accessToken == token) {
            throw ApplicationException(ErrorCode.INVALID_ACCESS_TOKEN)
        }
    }

    companion object {
        private const val AUTH_HEADER_NAME = "Authorization"
        private const val AUTH_HEADER_PREFIX = "Bearer "
        private const val USERNAME_ATTRIBUTE_KEY = "username"
        private const val TOKEN_ATTRIBUTE_KEY = "token"
    }
}
