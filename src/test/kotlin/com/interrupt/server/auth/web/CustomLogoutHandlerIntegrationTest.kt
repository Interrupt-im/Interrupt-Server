package com.interrupt.server.auth.web

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.auth.entity.TokenCache
import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.auth.service.JwtService
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

class CustomLogoutHandlerIntegrationTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var memberRepository: MemberRepository
    @Autowired
    private lateinit var jwtService: JwtService
    @Autowired
    private lateinit var tokenRepository: TokenRedisRepository

    @Test
    fun `로그아웃 요청을 받아 로그아웃을 수행한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "password", "name", "email"))
        val authenticationCredentials = jwtService.generateAuthenticationCredentials(member)
        val jti = authenticationCredentials.identifier.public
        val tokenCache = TokenCache(jti, authenticationCredentials, 100000000000L)

        tokenRepository.save(tokenCache)

        val entity = HttpHeaders().apply {
            set("Authorization", "Bearer ${authenticationCredentials.credentials.accessToken}")
        }.let {
            HttpEntity<String>("", it)
        }

        // when
        restTemplate.postForEntity<String>("http://localhost:$port/api/v1/auth/logout", entity)

        // then
        val result = tokenRepository.findById(jti)
        assertThat(result).isNull()
    }

}