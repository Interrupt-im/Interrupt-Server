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
        val foundVerifyCode = memberRecoverRepository.findByUuid(savedMemberRecover.uuid)
        Assertions.assertThat(foundVerifyCode).isNotNull.isEqualTo(memberRecover).isEqualTo(savedMemberRecover)
        Assertions.assertThat(foundVerifyCode!!.uuid).isNotNull()
    }

    @Test
    fun `이미 uuid 가 존재하는 인증코드는 기존 저장을 덮어쓴다`() {
        val memberRecover1 = MemberRecover("test@test.com", "test", "000000")
        val savedMemberRecover = memberRecoverRepository.save(memberRecover1)

        val memberRecover2 = MemberRecover("test@test.com", "test", "111111").apply { uuid = savedMemberRecover.uuid }

        // when
        memberRecoverRepository.save(memberRecover2)

        // then
        val updatedMemberRecover = memberRecoverRepository.findByUuid(memberRecover1.uuid)
        Assertions.assertThat(updatedMemberRecover).isNotNull.isEqualTo(savedMemberRecover).isEqualTo(memberRecover1).isEqualTo(memberRecover2)
        Assertions.assertThat(updatedMemberRecover!!.verifyCode).isEqualTo("111111")
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