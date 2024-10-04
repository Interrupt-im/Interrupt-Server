package com.interrupt.server.auth.presentation

import com.interrupt.server.auth.application.WhitelistUrl
import com.interrupt.server.auth.fake.FakeJwtService
import com.interrupt.server.auth.fake.FakeTokenProvider
import com.interrupt.server.auth.fixture.TokenFixture
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.member.fixture.MemberFixture
import com.interrupt.server.support.KotestUnitTestSupport
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.mockk
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class UrlWhitelistInterceptorTest : KotestUnitTestSupport() {
    private val interceptor = UrlWhitelistInterceptor(
        FakeJwtService(),
        tokenRepository,
        WhitelistUrl("/method/all"),
        WhitelistUrl("/uri/wild-card/**", listOf(HttpMethod.GET)),
        WhitelistUrl("/method/post", listOf(HttpMethod.POST)),
    )

    init {
        Given("http 메서드를 지정 하지 않은 url로 요청이 왔을 때") {

            withData(
                nameFn = { "요청 http 메서드 : $it" },
                HttpMethod.values().toList()
            ) {
                When("허용 된 회원 권한의 요청 이라면") {
                    val request = MockHttpServletRequest()
                    request.requestURI = "/method/all"
                    request.method = it.name()

                    val actual = interceptor.preHandle(request, MockHttpServletResponse(), mockk(relaxed = true))

                    Then("요청을 통과 시킨다") {
                        actual shouldBe true
                    }
                }
            }
        }

        Given("와일드 카드 패턴으로 매핑 된 url로 요청이 왔을 때") {

            When("해당 url 과 같은 계층의 url 의 경우") {
                val request = MockHttpServletRequest()
                request.requestURI = "/uri/wild-card"
                request.method = "GET"

                val actual = interceptor.preHandle(request, MockHttpServletResponse(), mockk(relaxed = true))

                Then("요청을 통과 시킨다") {
                    actual shouldBe true
                }
            }

            When("해당 url 의 하위 url 의 경우") {
                val request = MockHttpServletRequest()
                request.requestURI = "/uri/wild-card/foo"
                request.method = "GET"

                val actual = interceptor.preHandle(request, MockHttpServletResponse(), mockk(relaxed = true))

                Then("요청을 통과 시킨다") {
                    actual shouldBe true
                }
            }
        }

        Given("http 메서드가 지정 된 url 로 요청이 왔을 때") {

            When("지정 된 메서드로 온 요청이라면") {
                val request = MockHttpServletRequest()
                request.requestURI = "/method/post"
                request.method = HttpMethod.POST.name()

                val actual = interceptor.preHandle(request, MockHttpServletResponse(), mockk(relaxed = true))

                Then("요청을 통과 시킨다") {
                    actual shouldBe true
                }
            }

            withData(
                nameFn = { "요청 http 메서드 : $it" },
                HttpMethod.values().filterNot { it == HttpMethod.POST }.toList()
            ) {
                When("지정되지 않은 메서드로 온 요청이라면") {
                    val request = MockHttpServletRequest()
                    request.requestURI = "/method/post"
                    request.method = it.name()

                    Then("요청을 통과 시키지 않는다") {
                        shouldThrow<ApplicationException> {
                            interceptor.preHandle(request, MockHttpServletResponse(), mockk(relaxed = true))
                        } shouldHaveMessage "권한이 없습니다."
                    }
                }
            }
        }

        Given("화이트 리스트로 매핑 되지 않은 url 로 요청이 왔을 때") {

            When("헤더에 인증 정보가 올바르다면") {
                val request = MockHttpServletRequest()
                request.requestURI = "/not-match"
                request.method = "GET"
                FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 1`.email, TokenFixture.`토큰 1`.jti, 1000L).also {
                    val token = objectMapper.writeValueAsString(it)
                    request.addHeader("Authorization", "Bearer $token")
                }

                val actual = interceptor.preHandle(request, MockHttpServletResponse(), mockk(relaxed = true))

                Then("요청을 통과 시킨다") {
                    actual shouldBe true
                }
            }

            When("헤더에 인증 정보가 저장 되지 않은 토큰 이라면") {
                val request = MockHttpServletRequest()
                request.requestURI = "/not-match"
                request.method = "GET"
                FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 1`.email, TokenFixture.`저장 되지 않은 토큰`.jti, 1000L).also {
                    val token = objectMapper.writeValueAsString(it)
                    request.addHeader("Authorization", "Bearer $token")
                }

                Then("예외를 던진다") {
                    shouldThrow<ApplicationException> {
                        interceptor.preHandle(request, MockHttpServletResponse(), mockk(relaxed = true))
                    } shouldHaveMessage "액세스 토큰이 유효하지 않습니다."
                }
            }

            When("헤더에 인증 정보가 저장소에서 찾은 토큰과 다르다면") {
                val request = MockHttpServletRequest()
                request.requestURI = "/not-match"
                request.method = "GET"
                FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 2`.email, TokenFixture.`토큰 1`.jti, 1000L).also {
                    val token = objectMapper.writeValueAsString(it)
                    request.addHeader("Authorization", "Bearer $token")
                }

                Then("예외를 던진다") {
                    shouldThrow<ApplicationException> {
                        interceptor.preHandle(request, MockHttpServletResponse(), mockk(relaxed = true))
                    } shouldHaveMessage "액세스 토큰이 유효하지 않습니다."
                }
            }

            When("헤더에 인증 정보가 없다면") {
                val request = MockHttpServletRequest()
                request.requestURI = "/not-match"
                request.method = "GET"

                Then("예외를 던진다") {
                    shouldThrow<ApplicationException> {
                        interceptor.preHandle(request, MockHttpServletResponse(), mockk(relaxed = true))
                    } shouldHaveMessage "권한이 없습니다."
                }
            }
        }
    }
}
