package com.interrupt.server.auth.config

import com.interrupt.server.auth.application.JwtProperties
import com.interrupt.server.auth.application.JwtService
import com.interrupt.server.auth.application.TokenQueryRepository
import com.interrupt.server.auth.application.WhitelistUrl
import com.interrupt.server.auth.presentation.LoginUserArgumentResolver
import com.interrupt.server.auth.presentation.UrlWhitelistInterceptor
import com.interrupt.server.member.application.MemberDetailsService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class AuthConfig(
    private val jwtService: JwtService,
    private val tokenQueryRepository: TokenQueryRepository,
    private val memberDetailsService: MemberDetailsService,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(LoginUserArgumentResolver(memberDetailsService))
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(
            UrlWhitelistInterceptor(
                jwtService,
                tokenQueryRepository,
                WhitelistUrl(SIGH_UP_URL, listOf(HttpMethod.POST)),
                WhitelistUrl(LOGIN_URL, listOf(HttpMethod.POST)),
            )
        )
    }

    companion object {

        private const val SIGH_UP_URL = "/api/members"

        private const val LOGIN_URL = "/api/auth/login"

    }
}
