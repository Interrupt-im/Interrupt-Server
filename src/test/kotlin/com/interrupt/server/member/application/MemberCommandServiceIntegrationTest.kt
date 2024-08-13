package com.interrupt.server.member.application

import com.interrupt.server.member.fixture.MemberFixture
import com.interrupt.server.support.KotestIntegrationTestSupport
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.longs.shouldBeGreaterThan
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("MemberCommandService 통합 테스트")
class MemberCommandServiceIntegrationTest : KotestIntegrationTestSupport() {
    @Autowired
    private lateinit var memberCommandService: MemberCommandService

    init {
        Given("회원 생성 요청 객체를 받아") {
            val memberCommand = MemberFixture.`고객 1`.`회원 생성 COMMAND 생성`()

            When("회원 정보 저장 후") {
                val actual = memberCommandService.createMember(memberCommand)

                Then("저장 된 ID 를 반환 한다") {
                    actual shouldBeGreaterThan 0L
                }
            }
        }
    }
}
