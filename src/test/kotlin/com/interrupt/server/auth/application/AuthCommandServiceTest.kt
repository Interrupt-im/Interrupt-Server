package com.interrupt.server.auth.application

import com.interrupt.server.auth.fake.FakeJwtService
import com.interrupt.server.auth.fake.FakeTokenRepository
import com.interrupt.server.auth.fixture.TokenFixture
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.member.fake.FakeMemberQueryRepository
import com.interrupt.server.member.fake.FakePasswordService
import com.interrupt.server.member.fixture.MemberFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

@DisplayName("AuthCommandService 단위 테스트")
class AuthCommandServiceTest : BehaviorSpec({

    val memberQueryRepository = FakeMemberQueryRepository()
    val tokenCommandRepository = FakeTokenRepository()
    val authCommandService = AuthCommandService(memberQueryRepository, FakePasswordService(), FakeJwtService(), tokenCommandRepository)

    beforeAny {
        memberQueryRepository.init()
        tokenCommandRepository.init()
    }

    Given("로그인 요청을 받았을 때") {
        When("해당하는 회원이 존재 한다면") {
            val command = MemberFixture.`고객 1`.`로그인 COMMAND 생성`()

            val actual = authCommandService.login(command)

            Then("토큰을 생성 후 반환 한다") {
                actual.shouldNotBeNull()
            }
        }

        When("존재하는 회원 정보가 아니라면") {
            val command = MemberFixture.`삭제된 고객 1`.`로그인 COMMAND 생성`()

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    authCommandService.login(command)
                } shouldHaveMessage "아이디 또는 비밀번호를 잘못 입력 하셨습니다."
            }
        }
    }

    Given("로그아웃 요청을 받으면") {
        val command = TokenFixture.`토큰 1`.`로그 아웃 COMMAND 생성`()

        When("로그인 회원의 토큰을 이용해 토큰을 삭제한 후엔") {
            authCommandService.logout(command)

            val actual = tokenCommandRepository.findByJti("jti1")

            Then("저장소에서 해당 토큰을 조회할 수 없다") {
                actual shouldBe null
            }
        }
    }
})
