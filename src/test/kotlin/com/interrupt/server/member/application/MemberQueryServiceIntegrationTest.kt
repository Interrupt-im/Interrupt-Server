package com.interrupt.server.member.application

import com.interrupt.server.member.domain.Member
import com.interrupt.server.member.fixture.MemberFixture
import com.interrupt.server.member.infra.MemberJpaRepository
import com.interrupt.server.support.KotestIntegrationTestSupport
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

@DisplayName("MemberQueryService 통합 테스트")
class MemberQueryServiceIntegrationTest : KotestIntegrationTestSupport() {

    @Autowired
    private lateinit var memberQueryService: MemberQueryService

    @Autowired
    private lateinit var memberJpaRepository: MemberJpaRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    init {
        Given("회원 ID 로 회원을 찾을때") {
            val member = memberJpaRepository.saveAndFlush(MemberFixture.`고객 1`.`회원 엔티티 생성`(passwordEncoder))

            When("일치 하는 회원 정보가 있다면") {
                val actual = memberQueryService.findById(member.id)

                Then("같은 회원 정보를 반환 한다") {
                    actual.shouldBeEqualToUsingFields(member, Member::id, Member::email, Member::password, Member::nickname, Member::memberType)
                }
            }
        }
    }
}
