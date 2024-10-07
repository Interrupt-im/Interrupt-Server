package com.interrupt.server.member.application

import com.interrupt.server.member.domain.Password
import com.interrupt.server.support.KotestUnitTestSupport
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.shouldBe

@DisplayName("PasswordService 단위 테스트")
class PasswordServiceTest : KotestUnitTestSupport() {
    private val passwordService = PasswordService(passwordEncoder)

    init {
        Given("암호화 된 Password 객체와 암호화 되기 전 비밀번호 문자열이 같은 비밀번호인지 확인할 때") {
            val rawPassword = "1234abc!"
            val password = Password(rawPassword, passwordEncoder)

            When("같은 비밀번호인 경우") {
                val actual = passwordService.matches(rawPassword, password)

                Then("true 를 반환 한다") {
                    actual shouldBe true
                }
            }

            When("같은 비밀번호가 아닌 경우") {
                val nonMatchPassword = "1234abc@"

                val actual = passwordService.matches(nonMatchPassword, password)

                Then("false 를 반환 한다") {
                    actual shouldBe false
                }
            }
        }
    }
}
