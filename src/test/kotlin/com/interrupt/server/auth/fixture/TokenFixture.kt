package com.interrupt.server.auth.fixture

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.interrupt.server.auth.application.command.LogoutCommand
import com.interrupt.server.auth.domain.AuthenticationCredentials
import com.interrupt.server.auth.domain.Token
import com.interrupt.server.auth.fake.FakeTokenProvider
import com.interrupt.server.member.fixture.MemberFixture

enum class TokenFixture(
    val jti: String?,
    val accessToken: FakeTokenProvider.FakeTokenObject?,
    val refreshToken: FakeTokenProvider.FakeTokenObject?,
) {
    // 정상 토큰
    `토큰 1`("jti1", FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 1`.email, "jti1", 1000L), FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 1`.email, "jti1", 5000L)),
    `토큰 2`("jti2", FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 2`.email, "jti2", 1000L), FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 2`.email, "jti2", 5000L)),
    `저장 되지 않은 토큰`("jti2", FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 1`.email, "not-saved", 1000L), FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 1`.email, "not-saved", 5000L)),
    ;

    fun `토큰 엔티티 생성`(): Token = AuthenticationCredentials(jti!!, objectMapper.writeValueAsString(accessToken), objectMapper.writeValueAsString(refreshToken))

    fun `로그 아웃 COMMAND 생성`(): LogoutCommand = LogoutCommand(jti!!)

    companion object {
        private val objectMapper = jacksonObjectMapper()
    }
}
