package com.interrupt.server.auth.config

import com.interrupt.server.auth.web.JwtAuthenticationFilter
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector


@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties::class, RsaKeyProperties::class)
class SecurityConfiguration(
    private val jwtAuthFilter: JwtAuthenticationFilter,
    private val authenticationProvider: AuthenticationProvider,
    private val logoutHandler: LogoutHandler,
    private val entryPoint: AuthenticationEntryPoint,
    private val accessDeniedHandler: AccessDeniedHandler
) {

    private companion object {

        private val WHITE_LIST_URLS = arrayOf("/api/v1/auth/**", "/error")

    }

    @Bean
    fun mvcRequestMatcherBuilder(introspector: HandlerMappingIntrospector): MvcRequestMatcher.Builder {
        return MvcRequestMatcher.Builder(introspector)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, mvc: MvcRequestMatcher.Builder): SecurityFilterChain =
        http.run {
            csrf { it.disable() }
            headers { headers ->
                headers.frameOptions { it.disable() }
            }
            authorizeHttpRequests {
                it
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .requestMatchers(*createMvcRequestMatcherForWhitelist(mvc)).permitAll()
                    .requestMatchers(createMvcRequestMatcher("/api/v1/members", mvc, HttpMethod.POST)).permitAll()
                    .anyRequest()
                    .authenticated()
            }
            sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            authenticationProvider(authenticationProvider)
            addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            logout {
                it.logoutUrl("/api/v1/auth/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler { req, res, auth -> SecurityContextHolder.clearContext() }
//                    .logoutSuccessUrl()
            }
            exceptionHandling {
                it.authenticationEntryPoint(entryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            }
            build()
        }

    private fun createMvcRequestMatcher(url: String, mvc: MvcRequestMatcher.Builder, method: HttpMethod? = null): MvcRequestMatcher =
        mvc.pattern(url).apply { if (method != null) setMethod(method) }

    private fun createMvcRequestMatcherForWhitelist(mvc: MvcRequestMatcher.Builder): Array<MvcRequestMatcher> =
        WHITE_LIST_URLS.map { createMvcRequestMatcher(it, mvc) }.toTypedArray()

}