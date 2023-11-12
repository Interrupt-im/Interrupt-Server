package com.interrupt.server.career.skill.service

import com.interrupt.server.career.skill.dto.MemberSkillDto
import com.interrupt.server.career.skill.dto.SkillDto
import com.interrupt.server.career.skill.dto.SkillGroupDto
import com.interrupt.server.career.skill.entity.MemberSkill
import com.interrupt.server.career.skill.repository.MemberSkillRepository
import com.interrupt.server.career.skill.repository.SkillGroupRedisRepository
import com.interrupt.server.career.skill.repository.SkillQueryRepository
import com.interrupt.server.member.dto.MemberDto
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class SkillServiceTest {

    companion object {

        private val skillQueryRepository: SkillQueryRepository = mockk()
        private val skillGroupRedisRepository: SkillGroupRedisRepository = mockk()
        private val memberSkillRepository: MemberSkillRepository = mockk()
        private val skillService: SkillService = SkillService(skillQueryRepository, skillGroupRedisRepository, memberSkillRepository)

        @BeforeAll
        @JvmStatic
        fun setUpAll() {
            MockKAnnotations.init(this)
        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {
            clearAllMocks()
        }

    }

    @Test
    fun `기술 리스트 조회 시 레디스 저장소에 캐싱된 리스트가 있다면 캐싱 된 리스트를 반환한다`() {
        // given
        every { skillGroupRedisRepository.findAll() } returns listOf(SkillGroupDto(1, "언어"), SkillGroupDto(2, "프레임워크"))

        // when
        val skillList = skillService.getSkillList()

        // then
        assertThat(skillList)
            .hasSize(2)
            .isEqualTo(
                listOf(SkillGroupDto(1, "언어"), SkillGroupDto(2, "프레임워크"))
            )
    }

    @Test
    fun `기술 리스트 조회 시 레디스 저장소에 캐싱된 리스트가 없다면 DB 에서 조회해 레디스 저장소에 캐싱 후 반환한다`() {
        // given
        val skillGroupDto1 = SkillGroupDto(1, "언어")
        val skillGroupDto2 = SkillGroupDto(2, "프레임워크")

        every { skillGroupRedisRepository.findAll() } returns listOf()
        every { skillQueryRepository.findSkillDtoList() } returns listOf(
            SkillDto(1, "Java", skillGroupDto1), SkillDto(2, "Kotlin", skillGroupDto1),
            SkillDto(1, "Spring", skillGroupDto2)
        )
        justRun { skillGroupRedisRepository.saveAll(any<List<SkillGroupDto>>()) }

        // when
        val skillList = skillService.getSkillList()

        // then
        assertThat(skillList)
            .hasSize(2)
            .isEqualTo(
                listOf(SkillGroupDto(1, "언어"), SkillGroupDto(2, "프레임워크"))
            )
    }

    @Test
    fun `회원별 직무 리스트를 저장한다`() {
        // given
        val skillGroupDto1 = SkillGroupDto(1, "언어")
        val skillGroupDto2 = SkillGroupDto(2, "프레임워크")

        val skillDto1 = SkillDto(1, "Java", skillGroupDto1)
        val skillDto2 = SkillDto(2, "Kotlin", skillGroupDto1)
        val skillDto3 = SkillDto(1, "Spring", skillGroupDto2)

        val member = MemberDto(1, "memberId", "password", "name", "email@domain.com")

        val memberSkillDto1 = MemberSkillDto(member, skillDto1)
        val memberSkillDto2 = MemberSkillDto(member, skillDto2)
        val memberSkillDto3 = MemberSkillDto(member, skillDto3)

        every { memberSkillRepository.saveAll(any<List<MemberSkill>>()) } returns listOf()

        // when then
        skillService.registerMemberSkill(listOf(memberSkillDto1, memberSkillDto2, memberSkillDto3))
    }
}