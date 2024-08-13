package com.interrupt.server.member.application

import com.interrupt.server.member.fixture.MemberFixture
import com.interrupt.server.member.infra.MemberJpaRepository
import com.interrupt.server.support.KotestIntegrationTestSupport
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

@DisplayName("MemberQueryRepository 테스트")
class MemberQueryRepositoryTest : KotestIntegrationTestSupport() {

    @Autowired
    private lateinit var memberQueryRepository: MemberQueryRepository

    @Autowired
    private lateinit var memberJpaRepository: MemberJpaRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    init {
        Given("email 을 이용해") {
            val member = MemberFixture.`고객 1`.`회원 엔티티 생성`(passwordEncoder)
            val email = member.email

            When("이미 저장 된 회원 중 중복된 email 이 없는 경우") {
                val actual = memberQueryRepository.existsByEmailAndNotDeleted(email)

                Then("false 를 반환 한다") {
                    actual shouldBe false
                }
            }

            When("이미 저장 된 회원 중 중복된 email 이 있는 경우") {
                memberJpaRepository.save(member)
                val actual = memberQueryRepository.existsByEmailAndNotDeleted(email)

                Then("true 를 반환 한다") {
                    actual shouldBe true
                }
            }
        }
    }
}
