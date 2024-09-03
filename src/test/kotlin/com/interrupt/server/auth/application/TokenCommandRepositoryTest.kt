package com.interrupt.server.auth.application

import com.interrupt.server.auth.fixture.AuthenticationCredentialsFixture
import com.interrupt.server.support.KotestIntegrationTestSupport
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("TokenCommandRepository 통합 테스트")
class TokenCommandRepositoryTest : KotestIntegrationTestSupport() {

    @Autowired
    private lateinit var tokenCommandRepository: TokenCommandRepository

    @Autowired
    private lateinit var tokenQueryRepository: TokenQueryRepository

    init {
        Given("토큰 정보를 받아") {
            val token = AuthenticationCredentialsFixture.`토큰 1`.toToken()

            When("토큰을 저장소에 저장 하면") {
                val actual = tokenCommandRepository.save(token)
                val findByJti = tokenQueryRepository.findByJti(token.jti)

                Then("저장 된 토큰을 반환 한다") {
                    actual shouldBe token
                }

                Then("저장소에 토큰이 저장 되어 있다") {
                    findByJti.shouldNotBeNull()
                        .shouldBeEqualToUsingFields(token)
                }
            }
        }
    }
}
