package com.interrupt.server.auth.domain

import com.interrupt.server.auth.fixture.TokenFixture
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

@DisplayName("AuthenticationCredentials 테스트")
class AuthenticationCredentialsTest : BehaviorSpec({

    Given("객체의 refreshToken 상태 값과 전달 받은 refreshToken 값이 같은지 비교할 때") {
        val authenticationCredentials = TokenFixture.`토큰 1`.`토큰 엔티티 생성`()

        When("전달 받은 refreshToken 과 같은 경우") {
            val compare = TokenFixture.`토큰 1`.`토큰 엔티티 생성`()
            val actual = authenticationCredentials.isSameRefreshToken(compare.refreshToken)

            Then("true 를 반환 한다") {
                actual shouldBe true
            }
        }

        When("전달 받은 refreshToken 과 다른 경우") {
            val compare = TokenFixture.`토큰 2`.`토큰 엔티티 생성`()
            val actual = authenticationCredentials.isSameRefreshToken(compare.refreshToken)

            Then("false 를 반환 한다") {
                actual shouldBe false
            }
        }
    }
})
