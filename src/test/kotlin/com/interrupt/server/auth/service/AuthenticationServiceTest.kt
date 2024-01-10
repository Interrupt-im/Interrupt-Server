package com.interrupt.server.auth.service

import com.interrupt.server.auth.config.JwtProperties
import com.interrupt.server.auth.dto.login.SignInRequest
import com.interrupt.server.auth.entity.AuthenticationCredentials
import com.interrupt.server.auth.entity.Credentials
import com.interrupt.server.auth.entity.Identifier
import com.interrupt.server.auth.entity.TokenCache
import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

class AuthenticationServiceTest {

    private val tokenRedisRepository: TokenRedisRepository = mockk()
    private val memberQueryRepository: MemberRepository = mockk()
    private val jwtService: JwtService = mockk()
    private val authenticationManager: AuthenticationManager = mockk()
    private val jwtProperties: JwtProperties = mockk()
    private val authService: AuthenticationService = AuthenticationService(tokenRedisRepository, memberQueryRepository, jwtService, authenticationManager, jwtProperties)

    @Test
    fun `유저의 loginId 와 password 를 받아 로그인을 수행 후 토큰을 반환한다`() {
        // given
        val loginId = "loginId"
        val password = "password"
        val request = SignInRequest(loginId, password)

        val authentication: Authentication = mockk()
        val member: Member = mockk()
        val authenticationCredentials: AuthenticationCredentials = mockk()
        val identifier: Identifier = mockk()
        val key: String = "key"
        val expiration: Long = 1000L

        val accessToken = "accessToken"
        val refreshToken = "refreshToken"

        every { authenticationManager.authenticate(any< UsernamePasswordAuthenticationToken>()) } returns authentication
        every { authentication.principal } returns member
        every { jwtService.generateAuthenticationCredentials(member) } returns authenticationCredentials
        every { authenticationCredentials.identifier } returns identifier
        every { identifier.public } returns key
        every { jwtProperties.refreshTokenExpiration } returns expiration
        justRun { tokenRedisRepository.save(any<TokenCache>()) }
        every { authenticationCredentials.credentials } returns Credentials(accessToken, refreshToken)

        // when
        val response = authService.login(request)

        // then
        assertThat(response)
            .extracting("accessToken", "refreshToken")
            .contains(accessToken, refreshToken)
    }

}