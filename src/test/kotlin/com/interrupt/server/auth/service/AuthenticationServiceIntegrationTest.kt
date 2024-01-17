package com.interrupt.server.auth.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.auth.config.JwtProperties
import com.interrupt.server.auth.dto.login.SignInRequest
import com.interrupt.server.auth.dto.refresh.TokenRefreshRequest
import com.interrupt.server.auth.entity.AuthenticationCredentials
import com.interrupt.server.auth.entity.Credentials
import com.interrupt.server.auth.entity.Identifier
import com.interrupt.server.auth.entity.TokenCache
import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AuthenticationServiceIntegrationTest: IntegrationTestSupport() {
    @Autowired
    private lateinit var authService: AuthenticationService

    @Autowired
    private lateinit var encoder: PasswordEncoder
    @Autowired
    private lateinit var memberRepository: MemberRepository
    @Autowired
    private lateinit var jwtService: JwtService
    @Autowired
    private lateinit var jwtProperties: JwtProperties
    @Autowired
    private lateinit var tokenRedisRepository: TokenRedisRepository

    @Test
    fun `회원 가입 된 유저의 loginId 와 password 를 받아 로그인을 수행 후 토큰을 반환한다`() {
        // given
        val loginId = "loginId"
        val password = "password"
        memberRepository.save(Member(loginId, encoder.encode(password), "name", "email"))

        val request = SignInRequest(loginId, password)

        // when
        val response = authService.login(request)

        // then
        assertThat(response.accessToken).isNotBlank()
        assertThat(response.refreshToken).isNotBlank()
    }

    @Test
    fun `accessToken 이 만료됐을 시 refreshToken 을 받아 토큰을 재발급 한다`() {
        // given
        val loginId = "loginId"
        val password = "password"
        val member = memberRepository.save(Member(loginId, encoder.encode(password), "name", "email"))

        val jti = UUID.randomUUID().toString()
        val accessToken = jwtService.buildToken(member, jti, 1L)
        val refreshToken = jwtService.buildToken(member, jti, jwtProperties.refreshTokenExpiration)

        val authenticationCredentials = AuthenticationCredentials(Credentials(accessToken, refreshToken), Identifier(jti, member.id))

        val tokenCache = TokenCache(authenticationCredentials.identifier.public, authenticationCredentials, jwtProperties.refreshTokenExpiration)

        tokenRedisRepository.save(tokenCache)

        val request = TokenRefreshRequest(authenticationCredentials.credentials.refreshToken)

        // when
        val response = authService.refreshToken(request)

        // then
        assertThat(response.accessToken).isNotEqualTo(authenticationCredentials.credentials.accessToken)
        assertThat(response.refreshToken).isNotEqualTo(authenticationCredentials.credentials.refreshToken)
    }

}