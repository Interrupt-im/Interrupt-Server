package com.interrupt.server.auth.web

import com.interrupt.server.auth.entity.AuthenticationCredentials
import com.interrupt.server.auth.entity.Credentials
import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.auth.service.JwtService
import io.mockk.*
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class JwtAuthenticationFilterTest {

    private val jwtService: JwtService = mockk()
    private val userDetailsService: UserDetailsService = mockk()
    private val tokenRepository: TokenRedisRepository = mockk()

    private val jwtTokenFilter: JwtAuthenticationFilter = JwtAuthenticationFilter(jwtService, userDetailsService, tokenRepository)


    @Test
    fun `필터를 무시하는 경로로 요청이 오면 다음 필터를 바로 호출한다`() {
        // given
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/v1/auth/foo"
        }
        val response = MockHttpServletResponse()
        val filterChain: FilterChain = mockk(relaxed = true)
        val securityContext: SecurityContext = mockk()

        // when
        jwtTokenFilter.doFilterInternal(request, response, filterChain)

        // then
        verify(exactly = 0) { securityContext.authentication = any<Authentication>() }
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }

    @Test
    fun `Authorization 헤더가 없는 요청은 다음 필터를 바로 호출한다`() {
        // given
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/v1/members"
        }
        val response = MockHttpServletResponse()
        val filterChain: FilterChain = mockk(relaxed = true)
        val securityContext: SecurityContext = mockk()

        // when
        jwtTokenFilter.doFilterInternal(request, response, filterChain)

        // then
        verify(exactly = 0) { securityContext.authentication = any<Authentication>() }
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }

    @Test
    fun `Authorization 헤더 값이 'Bearer ' 로 시작하지 않는 요청은 다음 필터를 바로 호출한다`() {
        // given
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/v1/members"
            addHeader("Authorization", "Bearersdf890asf89asfd")
        }
        val response = MockHttpServletResponse()
        val filterChain: FilterChain = mockk(relaxed = true)
        val securityContext: SecurityContext = mockk()

        // when
        jwtTokenFilter.doFilterInternal(request, response, filterChain)

        // then
        verify(exactly = 0) { securityContext.authentication = any<Authentication>() }
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }

    @Test
    fun `헤더의 토큰과 헤더의 토큰 jti 로 토큰 저장소에서 토큰이 일치하지 않으면 다음 필터를 바로 호출한다`() {
        // given
        val jwt = "sdf890asf89asfd"
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/v1/members"
            addHeader("Authorization", "Bearer $jwt")
        }
        val response = MockHttpServletResponse()
        val filterChain: FilterChain = mockk(relaxed = true)
        val username = "username"
        val jti = "jti"
        val authenticationCredentials: AuthenticationCredentials = mockk()
        val credentials: Credentials = mockk()
        val securityContext: SecurityContext = mockk()

        every { jwtService.getUsername(jwt) } returns username
        every { jwtService.getJti(jwt) } returns jti
        every { tokenRepository.findById(jti) } returns authenticationCredentials
        every { authenticationCredentials.credentials } returns credentials
        every { credentials.accessToken } returns "accessToken"

        // when
        jwtTokenFilter.doFilterInternal(request, response, filterChain)

        // then
        verify(exactly = 0) { securityContext.authentication = any<Authentication>() }
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }

    @Test
    fun `SecurityContextHolder 의 SecurityContext 에서 Authentication 인증 객체가 null 이 아닌 경우 다음 필터를 바로 호출한다`() {
        // given
        val jwt = "sdf890asf89asfd"
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/v1/members"
            addHeader("Authorization", "Bearer $jwt")
        }
        val response = MockHttpServletResponse()
        val filterChain: FilterChain = mockk(relaxed = true)
        val username = "username"
        val jti = "jti"
        val authenticationCredentials: AuthenticationCredentials = mockk()
        val credentials: Credentials = mockk()
        val securityContext: SecurityContext = mockk()

        every { jwtService.getUsername(jwt) } returns username
        every { jwtService.getJti(jwt) } returns jti
        every { tokenRepository.findById(jti) } returns authenticationCredentials
        every { authenticationCredentials.credentials } returns credentials
        every { credentials.accessToken } returns jwt
        mockkStatic(SecurityContextHolder::class)
        every { SecurityContextHolder.getContext() } returns securityContext
        every { securityContext.authentication } returns mockk()

        // when
        jwtTokenFilter.doFilterInternal(request, response, filterChain)

        // then
        verify(exactly = 0) { securityContext.authentication = any<Authentication>() }
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }

    @Test
    fun `요청 헤더의 토큰이 유효성 검증을 통과하지 못하면 다음 필터를 바로 호출한다`() {
        // given
        val jwt = "sdf890asf89asfd"
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/v1/members"
            addHeader("Authorization", "Bearer $jwt")
        }
        val response = MockHttpServletResponse()
        val filterChain: FilterChain = mockk(relaxed = true)
        val username = "username"
        val jti = "jti"
        val authenticationCredentials: AuthenticationCredentials = mockk()
        val credentials: Credentials = mockk()
        val securityContext: SecurityContext = mockk()
        val userDetails: UserDetails = mockk()

        every { jwtService.getUsername(jwt) } returns username
        every { jwtService.getJti(jwt) } returns jti
        every { tokenRepository.findById(jti) } returns authenticationCredentials
        every { authenticationCredentials.credentials } returns credentials
        every { credentials.accessToken } returns jwt
        mockkStatic(SecurityContextHolder::class)
        every { SecurityContextHolder.getContext() } returns securityContext
        every { securityContext.authentication } returns null
        every { userDetailsService.loadUserByUsername(username) } returns userDetails
        every { jwtService.isTokenValid(jwt, userDetails) } returns false

        // when
        jwtTokenFilter.doFilterInternal(request, response, filterChain)

        // then
        verify(exactly = 0) { securityContext.authentication = any<Authentication>() }
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }

    @Test
    fun `정상적인 토큰을 포함한 요청인 경우 SecurityContext 를 업데이트 후 다음 필터를 호출한다`() {
        // given
        val jwt = "sdf890asf89asfd"
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/v1/members"
            addHeader("Authorization", "Bearer $jwt")
        }
        val response = MockHttpServletResponse()
        val filterChain: FilterChain = mockk(relaxed = true)
        val username = "username"
        val jti = "jti"
        val authenticationCredentials: AuthenticationCredentials = mockk()
        val credentials: Credentials = mockk()
        val securityContext: SecurityContext = mockk()
        val userDetails: UserDetails = mockk()

        every { jwtService.getUsername(jwt) } returns username
        every { jwtService.getJti(jwt) } returns jti
        every { tokenRepository.findById(jti) } returns authenticationCredentials
        every { authenticationCredentials.credentials } returns credentials
        every { credentials.accessToken } returns jwt
        mockkStatic(SecurityContextHolder::class)
        every { SecurityContextHolder.getContext() } returns securityContext
        every { securityContext.authentication } returns null
        every { userDetailsService.loadUserByUsername(username) } returns userDetails
        every { jwtService.isTokenValid(jwt, userDetails) } returns true
        every { userDetails.authorities } returns mockk(relaxed = true)
        justRun { securityContext.authentication = any<Authentication>() }

        // when
        jwtTokenFilter.doFilterInternal(request, response, filterChain)

        // then
        verify(exactly = 1) { securityContext.authentication = any<Authentication>() }
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }
}