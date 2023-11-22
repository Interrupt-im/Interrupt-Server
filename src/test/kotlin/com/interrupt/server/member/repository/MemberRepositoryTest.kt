package com.interrupt.server.member.repository

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.member.entity.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
class MemberRepositoryTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `회원 id 를 이용해 회원 엔티티 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        memberRepository.save(member)

        // when
        val foundMember = memberRepository.findByLoginId(member.loginId)
        val now = LocalDateTime.now()

        // then
        assertThat(foundMember)
            .isNotNull
            .isEqualTo(member)
            .extracting("id", "loginId", "password", "name", "email","deletedAt")
            .contains(member.id, member.loginId, member.password, member.name, member.email, null)
        assertThat(foundMember!!.createdAt).isBeforeOrEqualTo(now)
        assertThat(foundMember!!.modifiedAt).isBeforeOrEqualTo(now)
    }

    @Test
    fun `존재하지 않는 회원 id 를 이용해 회원 엔티티 찾을 때 null 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        // when
        val foundMember = memberRepository.findByLoginId(member.loginId)

        // then
        assertThat(foundMember).isNull()
    }

    @Test
    fun `회원 id 와 비밀번호 를 이용해 회원 엔티티 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        memberRepository.save(member)

        // when
        val foundMember = memberRepository.findByLoginIdAndPassword(member.loginId, member.password)
        val now = LocalDateTime.now()

        // then
        assertThat(foundMember)
            .isNotNull
            .isEqualTo(member)
            .extracting("id", "loginId", "password", "name", "email","deletedAt")
            .contains(member.id, member.loginId, member.password, member.name, member.email, null)
        assertThat(foundMember!!.createdAt).isBeforeOrEqualTo(now)
        assertThat(foundMember!!.modifiedAt).isBeforeOrEqualTo(now)
    }

    @Test
    fun `일치하는 회원 id 와 비밀번호 가진 레코드가 없을 때 null 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        // when
        val foundMember = memberRepository.findByLoginIdAndPassword(member.loginId, member.password)

        // then
        assertThat(foundMember).isNull()
    }

    @Test
    fun `회원 이름 와 이메일 주소 를 이용해 회원 엔티티 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        memberRepository.save(member)

        // when
        val foundMember = memberRepository.findByNameAndEmail(member.name, member.email)
        val now = LocalDateTime.now()

        // then
        assertThat(foundMember)
            .isNotNull
            .isEqualTo(member)
            .extracting("id", "loginId", "password", "name", "email","deletedAt")
            .contains(member.id, member.loginId, member.password, member.name, member.email, null)
        assertThat(foundMember!!.createdAt).isBeforeOrEqualTo(now)
        assertThat(foundMember!!.modifiedAt).isBeforeOrEqualTo(now)
    }

    @Test
    fun `일치하는 회원 이름과 이메일 주소를 가진 레코드가 없을 때 null 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        // when
        val foundMember = memberRepository.findByNameAndEmail(member.name, member.password)

        // then
        assertThat(foundMember).isNull()
    }

    @Test
    fun `회원 ID 와 이메일 주소 를 이용해 회원 엔티티 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        memberRepository.save(member)

        // when
        val foundMember = memberRepository.findByLoginIdAndEmail(member.loginId, member.email)
        val now = LocalDateTime.now()

        // then
        assertThat(foundMember)
            .isNotNull
            .isEqualTo(member)
            .extracting("id", "loginId", "password", "name", "email","deletedAt")
            .contains(member.id, member.loginId, member.password, member.name, member.email, null)
        assertThat(foundMember!!.createdAt).isBeforeOrEqualTo(now)
        assertThat(foundMember!!.modifiedAt).isBeforeOrEqualTo(now)
    }

    @Test
    fun `일치하는 회원 ID과 이메일 주소를 가진 레코드가 없을 때 null 반환`() {
        // given
        val member = Member("test1", "testPassword", "testName", "test@mail.com")

        // when
        val foundMember = memberRepository.findByLoginIdAndEmail(member.loginId, member.password)

        // then
        assertThat(foundMember).isNull()
    }

}