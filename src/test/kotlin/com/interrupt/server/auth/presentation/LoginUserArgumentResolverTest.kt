package com.interrupt.server.auth.presentation

import com.interrupt.server.auth.fake.FakeUserDetailsService
import com.interrupt.server.auth.fixture.TokenFixture
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.member.fake.FakeMemberQueryRepository
import com.interrupt.server.member.fixture.MemberFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.mockk
import org.springframework.core.MethodParameter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest

@DisplayName("LoginUserArgumentResolver 단위 테스트")
class LoginUserArgumentResolverTest : BehaviorSpec({

    val memberQueryRepository = FakeMemberQueryRepository()
    val loginUserArgumentResolver = LoginUserArgumentResolver(FakeUserDetailsService(memberQueryRepository))

    beforeAny {
        memberQueryRepository.init()
    }

    Given("요청이 왔을 때") {
        When("요청 정보에 유효한 username, 토큰 정보가 있다면") {
            val token = TokenFixture.`토큰 1`.`토큰 엔티티 생성`()

            val request = MockHttpServletRequest()
            request.setAttribute("username", MemberFixture.`고객 1`.email)
            request.setAttribute("token", token)

            val webRequest = ServletWebRequest(request)
            val methodParameter: MethodParameter = mockk()

            val actual = loginUserArgumentResolver.resolveArgument(methodParameter, null, webRequest, null)

            Then("UserDetails 를 반환 한다") {
                actual.shouldBeEqualToUsingFields(UserDetails(1L, "member1@domain.com"), UserDetails::username)
            }
        }

        When("요청 정보에 username 이 null 이라면") {
            val token = TokenFixture.`토큰 1`.`토큰 엔티티 생성`()

            val request = MockHttpServletRequest()
            request.setAttribute("token", token)

            val webRequest = ServletWebRequest(request)
            val methodParameter: MethodParameter = mockk()

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    loginUserArgumentResolver.resolveArgument(methodParameter, null, webRequest, null)
                } shouldHaveMessage "권한이 없습니다."
            }
        }

        When("요청 정보에 token 이 null 이라면") {
            val request = MockHttpServletRequest()
            request.setAttribute("username", MemberFixture.`고객 1`.email)

            val webRequest = ServletWebRequest(request)
            val methodParameter: MethodParameter = mockk()

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    loginUserArgumentResolver.resolveArgument(methodParameter, null, webRequest, null)
                } shouldHaveMessage "권한이 없습니다."
            }
        }
    }
})
