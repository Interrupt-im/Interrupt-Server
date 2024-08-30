package com.interrupt.server.member.presentation

import com.interrupt.server.member.fake.FakePasswordEncoder
import com.interrupt.server.member.fixture.MemberFixture
import com.interrupt.server.support.KotestControllerTestSupport
import io.kotest.core.annotation.DisplayName
import io.mockk.every
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

@DisplayName("MemberApi 테스트")
class MemberApiTest : KotestControllerTestSupport() {
    init {
        Given("회원 가입 요청이 왔을 때") {

            When("정상 적인 이메일, 비밀번호를 받았다면") {
                every { memberCommandService.createMember(any()) } returns 1L
                every { memberQueryService.findById(any()) } returns MemberFixture.`고객 1`.`회원 엔티티 생성`(FakePasswordEncoder.INSTANCE)

                val response =
                    mockMvc.post("/api/members") {
                        content = objectMapper.writeValueAsBytes(MemberFixture.`고객 1`.`회원 가입 요청 DTO 생성`())
                        contentType = MediaType.APPLICATION_JSON
                    }.andDo {
                        MockMvcResultHandlers.print()
                    }

                Then("201 상태 코드를 반환 한다") {
                    response.andExpect {
                        status { isCreated() }
                    }
                }

                Then("가입 된 회원의 id, email 을 반환 한다") {
                    response.andExpectAll {
                        jsonPath("$.data") { isNotEmpty() }
                        jsonPath("$.data.id") { isNumber() }
                        jsonPath("$.data.email") { isString() }
                    }
                }
            }
        }
    }
}
