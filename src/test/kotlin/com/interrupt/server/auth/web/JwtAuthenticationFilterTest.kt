package com.interrupt.server.auth.web

import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.auth.service.JwtService
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.userdetails.UserDetailsService

class JwtAuthenticationFilterTest {

    private val jwtService: JwtService = mockk()
    private val userDetailsService: UserDetailsService = mockk()
    private val tokenRepository: TokenRedisRepository = mockk()

    private val jwtTokenFilter: JwtAuthenticationFilter = JwtAuthenticationFilter(jwtService, userDetailsService, tokenRepository)

    @Test
    fun `필터를 무시하는 경로로 요청이 오면 다음 필터를 호출한다`() {
        // given
        val request = MockHttpServletRequest().apply {
            requestURI = "/api/v1/auth/foo"
        }
        val response = MockHttpServletResponse()
        val filterChain: FilterChain = mockk(relaxed = true)

        // when
        jwtTokenFilter.doFilterInternal(request, response, filterChain)

        // then
        verify(exactly = 1) { filterChain.doFilter(request, response) }
    }

}