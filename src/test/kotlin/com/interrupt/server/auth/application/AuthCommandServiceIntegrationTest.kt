package com.interrupt.server.auth.application

import com.interrupt.server.member.application.MemberCommandRepository
import com.interrupt.server.member.fixture.MemberFixture
import com.interrupt.server.support.KotestIntegrationTestSupport
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

@DisplayName("AuthCommandService 통합 테스트")
class AuthCommandServiceIntegrationTest : KotestIntegrationTestSupport() {

    @Autowired
    private lateinit var authCommandService: AuthCommandService

    @Autowired
    private lateinit var memberCommandRepository: MemberCommandRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    init {
        Given("이메일과 비밀번호를 받아") {
            val memberFixture = MemberFixture.`고객 1`
            val member = memberFixture.`회원 엔티티 생성`(passwordEncoder)
            memberCommandRepository.save(member)

            val command = memberFixture.`로그인 COMMAND 생성`()

            When("로그인을 하면") {
                val actual = authCommandService.login(command)

                Then("토큰을 반환 한다") {
                    actual.shouldNotBeNull()
                }
            }
        }
    }
}
