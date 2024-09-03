package com.interrupt.server.auth.application

import com.interrupt.server.auth.fake.FakeJwtService
import com.interrupt.server.auth.fake.FakeTokenCommandRepository
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.member.fake.FakeMemberQueryRepository
import com.interrupt.server.member.fake.FakePasswordService
import com.interrupt.server.member.fixture.MemberFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.throwable.shouldHaveMessage

@DisplayName("AuthCommandService 단위 테스트")
class AuthCommandServiceTest : BehaviorSpec({

    val memberQueryRepository = FakeMemberQueryRepository()
    val authCommandService = AuthCommandService(memberQueryRepository, FakePasswordService(), FakeJwtService(), FakeTokenCommandRepository())

    beforeAny {
        memberQueryRepository.init()
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

})
