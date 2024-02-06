package com.interrupt.server.auth.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class JwtServiceIntegrationTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var memberRepository: MemberRepository
    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Test
    fun `토큰을 받아서 username 을 반환한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()
        val token = tokenProvider.buildToken(member, jti, 1000L)

        // when
        val result = jwtService.getUsername(token)

        // then
        assertThat(result).isEqualTo(member.loginId)
    }

    @Test
    fun `토큰을 받아서 jti 을 반환한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()
        val token = tokenProvider.buildToken(member, jti, 1000L)

        // when
        val result = jwtService.getJti(token)

        // then
        assertThat(result).isEqualTo(jti)
    }

    @Test
    fun `userDetail 과 jti 를 받아 accessToken 을 생성한다`() {
        // given
        val userDetails = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()

        // when
        val accessToken = jwtService.generateAccessToken(userDetails, jti)

        // then
        assertThat(accessToken).isNotBlank()
    }

    @Test
    fun `userDetail 과 jti 를 받아 refreshToken 을 생성한다`() {
        // given
        val userDetails = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()

        // when
        val refreshToken = jwtService.generateRefreshToken(userDetails, jti)

        // then
        assertThat(refreshToken).isNotBlank()
    }

    @Test
    fun `member 객체를 받아 토큰 정보를 담은 AuthenticationCredentials 를 반환한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))

        // when
        val authenticationCredentials = jwtService.generateAuthenticationCredentials(member)

        // then
        assertThat(authenticationCredentials).isNotNull
    }

    @Test
    fun `토큰이 유효한지 검증한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()
        val token = tokenProvider.buildToken(member, jti, 1000L)

        // when
        val isTokenValid = jwtService.isTokenValid(token, member)

        // then
        assertThat(isTokenValid).isTrue()
    }

    @Test
    fun `토큰 문자열을 이용해 만료시간이 지나지 않았으면 true 를 반환한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()
        val token = tokenProvider.buildToken(member, jti, 100000000L)

        // when
        val isExpired = jwtService.checkTokenExpiredByTokenString(token)

        // then
        assertThat(isExpired).isFalse()
    }

}