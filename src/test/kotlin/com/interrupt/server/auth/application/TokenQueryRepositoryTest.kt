package com.interrupt.server.auth.application

import com.interrupt.server.auth.fixture.AuthenticationCredentialsFixture
import com.interrupt.server.support.KotestIntegrationTestSupport
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("TokenQueryRepository 통합 테스트")
class TokenQueryRepositoryTest : KotestIntegrationTestSupport() {

    @Autowired
    private lateinit var tokenQueryRepository: TokenQueryRepository

    @Autowired
    private lateinit var tokenCommandRepository: TokenCommandRepository

    init {
        Given("토큰의 key 값을 이용해 저장소에서 토큰을 조회할 때") {
            val token = AuthenticationCredentialsFixture.`토큰 1`.toToken()
            tokenCommandRepository.save(token)

            When("저장 된 토큰이 있다면") {
                val jti = AuthenticationCredentialsFixture.`토큰 1`.jti

                val actual = tokenQueryRepository.findByJti(jti)

                Then("반환 한다") {
                    actual.shouldNotBeNull()
                        .shouldBeEqualUsingFields(token)
                }
            }

            When("저장 된 토큰이 없다면") {
                val jti = AuthenticationCredentialsFixture.`토큰 2`.jti

                val actual = tokenQueryRepository.findByJti(jti)

                Then("null 을 반환 한다") {
                    actual.shouldBeNull()
                }
            }
        }
    }
}
