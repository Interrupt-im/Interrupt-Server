package com.interrupt.server.member.repository

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.member.entity.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
class MemberQueryRepositoryTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var memberRepository: MemberRepository
    @Autowired
    private lateinit var memberQueryRepository: MemberQueryRepository
    
    @Test
    fun `회원 id 를 이용해 회원 엔티티 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        memberRepository.save(member)

        // when
        val foundMember = memberQueryRepository.findByLoginId(member.loginId)
        val now = LocalDateTime.now()

        // then
        assertThat(foundMember)
            .isNotNull
            .isEqualTo(member)
            .extracting("id", "loginId", "password", "name", "email","deletedAt")
            .contains(member.id, member.loginId, member.loginPassword, member.name, member.email, null)
        assertThat(foundMember!!.createdAt).isBeforeOrEqualTo(now)
        assertThat(foundMember!!.modifiedAt).isBeforeOrEqualTo(now)
    }

    @Test
    fun `존재하지 않는 회원 id 를 이용해 회원 엔티티 찾을 때 null 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        // when
        val foundMember = memberQueryRepository.findByLoginId(member.loginId)

        // then
        assertThat(foundMember).isNull()
    }

    @Test
    fun `회원 이름 와 이메일 주소 를 이용해 회원 엔티티 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        memberRepository.save(member)

        // when
        val foundMember = memberQueryRepository.findByNameAndEmail(member.name, member.email)
        val now = LocalDateTime.now()

        // then
        assertThat(foundMember)
            .isNotNull
            .isEqualTo(member)
            .extracting("id", "loginId", "password", "name", "email","deletedAt")
            .contains(member.id, member.loginId, member.loginPassword, member.name, member.email, null)
        assertThat(foundMember!!.createdAt).isBeforeOrEqualTo(now)
        assertThat(foundMember!!.modifiedAt).isBeforeOrEqualTo(now)
    }

    @Test
    fun `일치하는 회원 이름과 이메일 주소를 가진 레코드가 없을 때 null 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        // when
        val foundMember = memberQueryRepository.findByNameAndEmail(member.name, member.loginPassword)

        // then
        assertThat(foundMember).isNull()
    }

    @Test
    fun `회원 ID 와 이메일 주소 를 이용해 회원 엔티티 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        memberRepository.save(member)

        // when
        val foundMember = memberQueryRepository.findByLoginIdAndEmail(member.loginId, member.email)
        val now = LocalDateTime.now()

        // then
        assertThat(foundMember)
            .isNotNull
            .isEqualTo(member)
            .extracting("id", "loginId", "password", "name", "email","deletedAt")
            .contains(member.id, member.loginId, member.loginPassword, member.name, member.email, null)
        assertThat(foundMember!!.createdAt).isBeforeOrEqualTo(now)
        assertThat(foundMember!!.modifiedAt).isBeforeOrEqualTo(now)
    }

    @Test
    fun `일치하는 회원 ID과 이메일 주소를 가진 레코드가 없을 때 null 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        // when
        val foundMember = memberQueryRepository.findByLoginIdAndEmail(member.loginId, member.loginPassword)

        // then
        assertThat(foundMember).isNull()
    }

}