package com.interrupt.server.auth.repository

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.auth.entity.AuthenticationCredentials
import com.interrupt.server.auth.entity.Credentials
import com.interrupt.server.auth.entity.Identifier
import com.interrupt.server.auth.entity.TokenCache
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TokenRedisRepositoryTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var tokenRedisRepository: TokenRedisRepository

    @AfterEach
    fun tearDown() {
        tokenRedisRepository.deleteAll()
    }

    @Test
    fun `TokenCache 를 받아서 토큰 정보를 저장한다`() {
        // given
        val tokenCache = TokenCache(
            key = "key",
            value = AuthenticationCredentials(
                credentials = Credentials(
                    accessToken = "accessToken",
                    refreshToken = "refreshToken"
                ),
                identifier = Identifier(
                    public = "key",
                    private = 1L
                )
            ),
            ttl = 1000L
        )

        // when then
        tokenRedisRepository.save(tokenCache)
    }

    @Test
    fun `key 를 이용해 저장된 토큰을 조회한다`() {
        // given
        val tokenCache = TokenCache(
            key = "key",
            value = AuthenticationCredentials(
                credentials = Credentials(
                    accessToken = "accessToken",
                    refreshToken = "refreshToken"
                ),
                identifier = Identifier(
                    public = "key",
                    private = 1L
                )
            ),
            ttl = 1000L
        )
        tokenRedisRepository.save(tokenCache)

        // when
        val authenticationCredentials = tokenRedisRepository.findById(tokenCache.key)

        // then
        assertThat(authenticationCredentials).isEqualTo(tokenCache.value)
        assertThat(authenticationCredentials!!.credentials)
            .extracting("accessToken", "refreshToken")
            .contains(tokenCache.value.credentials.accessToken, tokenCache.value.credentials.refreshToken)
        assertThat(authenticationCredentials.identifier)
            .extracting("public", "private")
            .contains(tokenCache.value.identifier.public, tokenCache.value.identifier.private)
    }

    @Test
    fun `key 를 이용해 토큰을 조회할 때, 저장된 토큰이 없으면 null 을 반환한다`() {
        // given
        val key = "key"

        // when
        val authenticationCredentials = tokenRedisRepository.findById(key)

        // then
        assertThat(authenticationCredentials).isNull()
    }

    @Test
    fun `key 를 이용해 저장된 토큰을 삭제한다`() {
        // given
        val tokenCache = TokenCache(
            key = "key",
            value = AuthenticationCredentials(
                credentials = Credentials(
                    accessToken = "accessToken",
                    refreshToken = "refreshToken"
                ),
                identifier = Identifier(
                    public = "key",
                    private = 1L
                )
            ),
            ttl = 1000L
        )
        tokenRedisRepository.save(tokenCache)

        // when then
        tokenRedisRepository.deleteById(tokenCache.key)
    }

    @Test
    fun `저장된 모든 토큰을 삭제한다`() {
        // given
        val tokenCache1 = TokenCache(
            key = "key1",
            value = AuthenticationCredentials(
                credentials = Credentials(
                    accessToken = "accessToken1",
                    refreshToken = "refreshToken1"
                ),
                identifier = Identifier(
                    public = "key1",
                    private = 1L
                )
            ),
            ttl = 1000L
        )
        val tokenCache2 = TokenCache(
            key = "key2",
            value = AuthenticationCredentials(
                credentials = Credentials(
                    accessToken = "accessToken2",
                    refreshToken = "refreshToken2"
                ),
                identifier = Identifier(
                    public = "key2",
                    private = 2L
                )
            ),
            ttl = 1000L
        )
        val tokenCache3 = TokenCache(
            key = "key3",
            value = AuthenticationCredentials(
                credentials = Credentials(
                    accessToken = "accessToken3",
                    refreshToken = "refreshToken3"
                ),
                identifier = Identifier(
                    public = "key3",
                    private = 3L
                )
            ),
            ttl = 1000L
        )
        val tokenCache4 = TokenCache(
            key = "key4",
            value = AuthenticationCredentials(
                credentials = Credentials(
                    accessToken = "accessToken4",
                    refreshToken = "refreshToken4"
                ),
                identifier = Identifier(
                    public = "key4",
                    private = 4L
                )
            ),
            ttl = 1000L
        )
        val tokenCache5 = TokenCache(
            key = "key5",
            value = AuthenticationCredentials(
                credentials = Credentials(
                    accessToken = "accessToken5",
                    refreshToken = "refreshToken5"
                ),
                identifier = Identifier(
                    public = "key5",
                    private = 5L
                )
            ),
            ttl = 1000L
        )

        tokenRedisRepository.save(tokenCache1)
        tokenRedisRepository.save(tokenCache2)
        tokenRedisRepository.save(tokenCache3)
        tokenRedisRepository.save(tokenCache4)
        tokenRedisRepository.save(tokenCache5)

        // when
        tokenRedisRepository.deleteAll()

        // then
        val authenticationCredentials1 = tokenRedisRepository.findById(tokenCache1.key)
        val authenticationCredentials2 = tokenRedisRepository.findById(tokenCache2.key)
        val authenticationCredentials3 = tokenRedisRepository.findById(tokenCache3.key)
        val authenticationCredentials4 = tokenRedisRepository.findById(tokenCache4.key)
        val authenticationCredentials5 = tokenRedisRepository.findById(tokenCache5.key)

        assertThat(authenticationCredentials1).isNull()
        assertThat(authenticationCredentials2).isNull()
        assertThat(authenticationCredentials3).isNull()
        assertThat(authenticationCredentials4).isNull()
        assertThat(authenticationCredentials5).isNull()
    }

}