package com.interrupt.server.member.application

import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.member.domain.Member
import com.interrupt.server.member.domain.MemberType
import com.interrupt.server.member.domain.Password
import com.interrupt.server.member.fixture.MemberFixture
import com.interrupt.server.support.KotestUnitTestSupport
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

@DisplayName("MemberQueryService 테스트")
class MemberQueryServiceTest : KotestUnitTestSupport() {
    private val memberQueryService = MemberQueryService(memberQueryRepository)

    init{
        Given("회원 ID 를 이용해 탈퇴 하지 않은 회원을 조회할 때") {
            When("ID 가 일치 하면서 탈퇴 하지 않은 회원이 존재 한다면") {
                val member = memberCommandRepository.save(MemberFixture.`고객 1`.`회원 엔티티 생성`(passwordEncoder))

                val actual = memberQueryService.findById(member.id)

                Then("회원 엔티티를 반환한다") {
                    actual.shouldNotBeNull()
                        .shouldBeEqualToUsingFields(
                            Member("member1@domain.com", Password("password123", passwordEncoder), "nickname", MemberType.CUSTOMER),
                            Member::email,
                            Member::password,
                            Member::nickname,
                            Member::memberType,
                        )
                }
            }

            When("ID 가 일치 하면서 탈퇴 하지 않은 회원이 없다면") {
                Then("예외를 던진다") {
                    shouldThrow<ApplicationException> {
                        memberQueryService.findById(0L)
                    }.message shouldBe "일치하는 회원 정보를 찾을 수 없습니다."
                }
            }
        }
    }
}
