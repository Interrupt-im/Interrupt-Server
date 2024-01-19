package com.interrupt.server.auth.service

import com.interrupt.server.auth.config.JwtProperties
import com.interrupt.server.auth.dto.login.SignInRequest
import com.interrupt.server.auth.dto.refresh.TokenRefreshRequest
import com.interrupt.server.auth.entity.AuthenticationCredentials
import com.interrupt.server.auth.entity.Credentials
import com.interrupt.server.auth.entity.Identifier
import com.interrupt.server.auth.entity.TokenCache
import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

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

    @Test
    fun `refreshToken 을 받아 토큰을 재발급 한다`() {
        // given
        val originAccessToken = "originAccessToken"
        val originRefreshToken = "originRefreshToken"
        val request = TokenRefreshRequest(originRefreshToken)
        val loginId = "loginId"
        val member = mockk<Member>()
        val originAuthenticationCredentials: AuthenticationCredentials = mockk()
        val originCredentials: Credentials = mockk()
        val newAuthenticationCredentials = mockk<AuthenticationCredentials>()
        val newIdentifier = mockk<Identifier>()
        val expiration: Long = 1000L

        val newAccessToken = "newAccessToken"
        val newRefreshToken = "newRefreshToken"

        every { jwtService.getUsername(any<String>()) } returns loginId
        every { jwtService.getJti(any<String>()) } returns "key"
        every { memberQueryRepository.findByLoginId(any<String>()) } returns member
        every { tokenRedisRepository.findById(any<String>()) } returns originAuthenticationCredentials
        every { originAuthenticationCredentials.credentials } returns originCredentials
        every { originCredentials.refreshToken } returns originRefreshToken
        every { jwtService.isTokenValid(any<String>(), any<UserDetails>()) } returns true
        every { originCredentials.accessToken } returns originAccessToken
        every { jwtService.checkTokenExpiredByTokenString(any<String>()) } returns true
        every { member.loginId } returns loginId
        every { tokenRedisRepository.deleteById(any<String>()) } returns true
        every { jwtService.generateAuthenticationCredentials(any<Member>()) } returns newAuthenticationCredentials
        every { newAuthenticationCredentials.identifier } returns newIdentifier
        every { newIdentifier.public } returns "newKey"
        every { jwtProperties.refreshTokenExpiration } returns expiration
        justRun { tokenRedisRepository.save(any<TokenCache>()) }
        every { newAuthenticationCredentials.credentials } returns Credentials(newAccessToken, newRefreshToken)

        // when
        val result = authService.refreshToken(request)

        // then
        assertThat(result)
            .extracting("accessToken", "refreshToken")
            .contains(newAccessToken, newRefreshToken)
    }

    @Test
    fun `토큰 재발급 시 전달 받은 refreshToken 과 토큰에 포함된 jti 로 저장소에서 찾은 refreshToken 이 다른 경우 예외를 반환한다`() {
        // given
        val originRefreshToken = "originRefreshToken"
        val request = TokenRefreshRequest(originRefreshToken)
        val loginId = "loginId"
        val member = mockk<Member>()
        val originAuthenticationCredentials: AuthenticationCredentials = mockk()
        val originCredentials: Credentials = mockk()

        every { jwtService.getUsername(any<String>()) } returns loginId
        every { jwtService.getJti(any<String>()) } returns "key"
        every { memberQueryRepository.findByLoginId(any<String>()) } returns member
        every { tokenRedisRepository.findById(any<String>()) } returns originAuthenticationCredentials
        every { originAuthenticationCredentials.credentials } returns originCredentials
        every { originCredentials.refreshToken } returns "invalidRefreshToken"

        // when then
        assertThatThrownBy { authService.refreshToken(request) }
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED.message)
    }

    @Test
    fun `토큰 재발급 시 전달 받은 refreshToken 이 유효하지 않다면 예외를 반환한다`() {
        // given
        val originRefreshToken = "originRefreshToken"
        val request = TokenRefreshRequest(originRefreshToken)
        val loginId = "loginId"
        val member = mockk<Member>()
        val originAuthenticationCredentials: AuthenticationCredentials = mockk()
        val originCredentials: Credentials = mockk()

        every { jwtService.getUsername(any<String>()) } returns loginId
        every { jwtService.getJti(any<String>()) } returns "key"
        every { memberQueryRepository.findByLoginId(any<String>()) } returns member
        every { tokenRedisRepository.findById(any<String>()) } returns originAuthenticationCredentials
        every { originAuthenticationCredentials.credentials } returns originCredentials
        every { originCredentials.refreshToken } returns originRefreshToken
        every { jwtService.isTokenValid(originRefreshToken, any<UserDetails>()) } returns false

        // when then
        assertThatThrownBy { authService.refreshToken(request) }
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED.message)
    }

    @Test
    fun `토큰 재발급 시 전달 받은 토큰의 jti 로 찾은 accessToken 이 만료되기 전 이면 예외를 반환한다`() {
        // given
        val originAccessToken = "originAccessToken"
        val originRefreshToken = "originRefreshToken"
        val request = TokenRefreshRequest(originRefreshToken)
        val loginId = "loginId"
        val member = mockk<Member>()
        val originAuthenticationCredentials: AuthenticationCredentials = mockk()
        val originCredentials: Credentials = mockk()

        every { jwtService.getUsername(any<String>()) } returns loginId
        every { jwtService.getJti(any<String>()) } returns "key"
        every { memberQueryRepository.findByLoginId(any<String>()) } returns member
        every { tokenRedisRepository.findById(any<String>()) } returns originAuthenticationCredentials
        every { originAuthenticationCredentials.credentials } returns originCredentials
        every { originCredentials.refreshToken } returns originRefreshToken
        every { jwtService.isTokenValid(any<String>(), any<UserDetails>()) } returns true
        every { originCredentials.accessToken } returns originAccessToken
        every { jwtService.checkTokenExpiredByTokenString(originAccessToken) } returns false

        // when then
        assertThatThrownBy { authService.refreshToken(request) }
            .isInstanceOf(InterruptServerException::class.java)
            .hasMessage(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED.message)
    }

}