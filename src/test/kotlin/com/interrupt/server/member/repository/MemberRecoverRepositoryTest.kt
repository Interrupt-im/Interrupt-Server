package com.interrupt.server.member.repository

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.member.entity.MemberRecover
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MemberRecoverRepositoryTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var memberRecoverRepository: MemberRecoverRepository

    @Test
    fun `이메일 인증코드를 저장한다`() {
        // given
        val memberRecover = MemberRecover("test@test.com", "test", "000000")

        // when
        val savedMemberRecover = memberRecoverRepository.save(memberRecover)

        // then
        Assertions.assertThat(savedMemberRecover).isEqualTo(memberRecover)
        Assertions.assertThat(savedMemberRecover.uuid).isNotNull()
    }

    @Test
    fun `uuid 를 이용해 이메일 인증코드를 반환받는다`() {
        // given
        val memberRecover = MemberRecover("test@test.com", "test", "000000")
        memberRecoverRepository.save(memberRecover)

        // when
        val foundMemberRecover = memberRecoverRepository.findByUuid(memberRecover.uuid)

        // then
        Assertions.assertThat(foundMemberRecover).isEqualTo(memberRecover)
    }

}