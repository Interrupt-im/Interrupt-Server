package com.interrupt.server.auth.presentation

import com.interrupt.server.auth.application.WhitelistUrl
import com.interrupt.server.member.domain.MemberType
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class UrlWhitelistInterceptorTest : BehaviorSpec({

    val interceptor = UrlWhitelistInterceptor(
        WhitelistUrl("/method/all"),
        WhitelistUrl("/uri/wild-card/**", listOf(HttpMethod.GET)),
        WhitelistUrl("/method/post", listOf(HttpMethod.POST)),
        WhitelistUrl("/role/seller", role = MemberType.SELLER),
    )

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
})
