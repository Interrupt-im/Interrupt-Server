package com.interrupt.server.auth.presentation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.interrupt.server.auth.fake.FakeJwtService
import com.interrupt.server.auth.fake.FakeTokenProvider
import com.interrupt.server.auth.fake.FakeTokenRepository
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

    val tokenQueryRepository = FakeTokenRepository()
    val memberQueryRepository = FakeMemberQueryRepository()
    val loginUserArgumentResolver = LoginUserArgumentResolver(FakeJwtService(), tokenQueryRepository, FakeUserDetailsService(memberQueryRepository))

    val objectMapper = jacksonObjectMapper()

    beforeAny {
        tokenQueryRepository.init()
        memberQueryRepository.init()
    }

    Given("요청이 왔을 때") {
        When("유효한 토큰이 포함 된 요청 이라면") {
            val request = MockHttpServletRequest()
            FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 1`.email, TokenFixture.`토큰 1`.jti, 1000L).also {
                val token = objectMapper.writeValueAsString(it)
                request.addHeader("Authorization", "Bearer $token")
            }

            val webRequest = ServletWebRequest(request)
            val methodParameter: MethodParameter = mockk()

            val actual = loginUserArgumentResolver.resolveArgument(methodParameter, null, webRequest, null)

            Then("UserDetails 를 반환 한다") {
                actual.shouldBeEqualToUsingFields(UserDetails(1L, "member1@domain.com"), UserDetails::username)
            }
        }

        When("요청에 포함 된 토큰이 저장 되지 않은 토큰 이라면") {
            val request = MockHttpServletRequest()
            FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 1`.email, TokenFixture.`저장 되지 않은 토큰`.jti, 1000L).also {
                val token = objectMapper.writeValueAsString(it)
                request.addHeader("Authorization", "Bearer $token")
            }

            val webRequest = ServletWebRequest(request)
            val methodParameter: MethodParameter = mockk()

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    loginUserArgumentResolver.resolveArgument(methodParameter, null, webRequest, null)
                } shouldHaveMessage "액세스 토큰이 유효하지 않습니다."
            }
        }

        When("요청에 포함 된 토큰이 저장소에서 찾은 토큰과 다르다면") {
            val request = MockHttpServletRequest()
            FakeTokenProvider.FakeTokenObject(MemberFixture.`고객 2`.email, TokenFixture.`토큰 1`.jti, 1000L).also {
                val token = objectMapper.writeValueAsString(it)
                request.addHeader("Authorization", "Bearer $token")
            }

            val webRequest = ServletWebRequest(request)
            val methodParameter: MethodParameter = mockk()

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    loginUserArgumentResolver.resolveArgument(methodParameter, null, webRequest, null)
                } shouldHaveMessage "액세스 토큰이 유효하지 않습니다."
            }
        }
    }
})
