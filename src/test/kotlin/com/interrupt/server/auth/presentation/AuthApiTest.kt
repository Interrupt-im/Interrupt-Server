package com.interrupt.server.auth.presentation

import com.interrupt.server.auth.presentation.dto.request.TokenRefreshRequest
import com.interrupt.server.auth.presentation.dto.response.LoginResponse
import com.interrupt.server.auth.presentation.dto.response.TokenRefreshResponse
import com.interrupt.server.global.common.SuccessResponse
import com.interrupt.server.member.fixture.MemberFixture
import com.interrupt.server.support.KotestControllerTestSupport
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.beEmpty
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

@DisplayName("AuthApi 테스트")
class AuthApiTest : KotestControllerTestSupport() {

    init {
        Given("로그인 요청이 왔을 때") {
            When("올바른 이메일과 비밀번호로 요청이 오면") {
                val actual = client.post("/api/auth/login") {
                    setBody(MemberFixture.`고객 1`.`로그인 요청 DTO 생성`())
                }

                Then("200 상태 코드를 반환 한다") {
                    actual.status shouldBe HttpStatusCode.OK
                }

                Then("토큰 쌍을 반환 한다") {
                    val body: SuccessResponse<LoginResponse> = actual.body()

                    assertSoftly {
                        body.data shouldNotBe null
                        body.data!!.accessToken shouldNot beEmpty()
                        body.data!!.refreshToken shouldNot beEmpty()
                    }
                }
            }
        }

        Given("로그아웃 요청이 왔을 때") {
            WhenWithAuth("로그인 된 회원의 요청 이라면") {
                val actual = client.post("/api/auth/logout")

                Then("204 상태 코드를 반환 한다") {
                    actual.status shouldBe HttpStatusCode.NoContent
                }
            }

            When("로그인 되지 않은 요청 이라면") {
                val actual = client.post("/api/auth/logout")

                Then("401 상태 코드를 반환 한다") {
                    actual.status shouldBe HttpStatusCode.Unauthorized
                }
            }
        }

        Given("토큰 재발급 요청이 왔을 때") {
            WhenWithAuth("로그인 된 회원의 요청 이라면") {
                val actual = client.post("/api/auth/refresh") {
                    setBody(TokenRefreshRequest(refreshToken))
                }

                Then("200 상태 코드를 반환 한다") {
                    actual.status shouldBe HttpStatusCode.OK
                }

                Then("새로운 토큰을 응답 한다") {
                    val body: SuccessResponse<TokenRefreshResponse> = actual.body()

                    assertSoftly {
                        body.data shouldNotBe null
                        body.data!!.accessToken shouldNot beEmpty()
                        body.data!!.refreshToken shouldNot beEmpty()
                    }
                }
            }

            When("로그인 되지 않은 요청 이라면") {

            }
        }
    }
}
