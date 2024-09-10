package com.interrupt.server.auth.presentation

import com.interrupt.server.auth.application.JwtService
import com.interrupt.server.auth.application.TokenQueryRepository
import com.interrupt.server.auth.application.UserDetailsService
import com.interrupt.server.auth.domain.Token
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class LoginUserArgumentResolver(
    private val jwtService: JwtService,
    private val tokenQueryRepository: TokenQueryRepository,
    private val userDetailsService: UserDetailsService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean = parameter.hasParameterAnnotation(LoginUser::class.java)

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any {
        val authorization = webRequest.getHeader(AUTH_HEADER_NAME)

        if (!authorization.isNullOrBlank() && authorization.startsWith(AUTH_HEADER_PREFIX)) {
            val token = authorization.substring(AUTH_HEADER_PREFIX.length)
            val username = jwtService.getUsername(token)
            val jti = jwtService.getJti(token)
            val savedToken = tokenQueryRepository.findByJti(jti)

            checkValidAccessToken(token, savedToken)

            val userDetails = userDetailsService.loadUserByUsername(username) ?: throw ApplicationException(ErrorCode.INVALID_ACCESS_TOKEN)
            userDetails.setToken(savedToken!!)

            return userDetails
        }

        throw ApplicationException(ErrorCode.INVALID_ACCESS_TOKEN)
    }

    private fun checkValidAccessToken(token: String, savedToken: Token?) {
        check(savedToken != null && savedToken.accessToken == token) {
            throw ApplicationException(ErrorCode.INVALID_ACCESS_TOKEN)
        }
    }

    companion object {
        private const val AUTH_HEADER_NAME = "Authorization"
        private const val AUTH_HEADER_PREFIX = "Bearer "
    }
}
