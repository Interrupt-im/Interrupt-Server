package com.interrupt.server.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.interrupt.server.auth.application.TokenCommandRepository
import com.interrupt.server.auth.application.TokenQueryRepository
import com.interrupt.server.auth.fake.FakeTokenRepository
import com.interrupt.server.member.application.MemberCommandRepository
import com.interrupt.server.member.application.MemberQueryRepository
import com.interrupt.server.member.fake.FakeMemberRepository
import com.interrupt.server.member.fake.FakePasswordEncoder
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.security.crypto.password.PasswordEncoder

abstract class KotestUnitTestSupport : BehaviorSpec() {
    protected val memberRepository = FakeMemberRepository()
    protected val memberQueryRepository: MemberQueryRepository
        get() = memberRepository
    protected val memberCommandRepository: MemberCommandRepository
        get() = memberRepository

    protected val tokenRepository = FakeTokenRepository()
    protected val tokenQueryRepository: TokenQueryRepository
        get() = tokenRepository
    protected val tokenCommandRepository: TokenCommandRepository
        get() = tokenRepository

    protected val objectMapper: ObjectMapper = jacksonObjectMapper()
    protected val passwordEncoder: PasswordEncoder = FakePasswordEncoder()

    init {
        afterContainer {
            if (it.a.isWhen()) {
                memberRepository.init()
                tokenRepository.init()
            }
        }
    }
}
