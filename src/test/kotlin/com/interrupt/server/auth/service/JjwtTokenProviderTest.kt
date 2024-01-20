package com.interrupt.server.auth.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class JjwtTokenProviderTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `토큰에서 username 을 추출한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()
        val token = tokenProvider.buildToken(member, jti, 1000L)

        // when
        val result = tokenProvider.extractUsername(token)

        // then
        assertThat(result).isEqualTo(member.loginId)
    }

    @Test
    fun `토큰에서 jti 을 추출한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()
        val token = tokenProvider.buildToken(member, jti, 1000L)

        // when
        val result = tokenProvider.extractJti(token)

        // then
        assertThat(result).isEqualTo(jti)
    }

    @Test
    fun `토큰에서 만료시간을 추출한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()
        val token = tokenProvider.buildToken(member, jti, 1000L)

        // when
        val result = tokenProvider.extractExpiration(token)

        // then
        assertThat(result).isAfter(Date())
    }

    @Test
    fun `토큰을 생성한다`() {
        // given
        val member = memberRepository.save(Member("loginId", "loginPassword", "name", "email"))
        val jti = UUID.randomUUID().toString()

        // when
        val result = tokenProvider.buildToken(member, jti, 1000L)

        // then
        assertThat(result).isNotBlank()
    }

}