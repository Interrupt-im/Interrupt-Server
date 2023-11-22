package com.interrupt.server.career.skill.repository

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.career.skill.dto.SkillGroupDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SkillGroupRedisRepositoryTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var skillGroupRedisRepository: SkillGroupRedisRepository

    @Test
    fun `기술 그룹을 저장한다`() {
        // given
        val skillGroupDto = SkillGroupDto(1, "언어")

        // when then
        skillGroupRedisRepository.save(skillGroupDto)
    }

    @Test
    fun `기슬 그룹 리스트를 저장한다`() {
        // given
        val skillGroupDto1 = SkillGroupDto(1, "언어")
        val skillGroupDto2 = SkillGroupDto(2, "프레임워크")

        // when then
        skillGroupRedisRepository.saveAll(listOf(skillGroupDto1, skillGroupDto2))
    }

    @Test
    fun `저장된 기술 그룹 리스트를 반환한다`() {
        // given
        val skillGroupDto1 = SkillGroupDto(1, "언어")
        val skillGroupDto2 = SkillGroupDto(2, "프레임워크")
        skillGroupRedisRepository.saveAll(listOf(skillGroupDto1, skillGroupDto2))

        // when
        val skillGroupDtos = skillGroupRedisRepository.findAll()

        // then
        assertThat(skillGroupDtos)
            .hasSize(2)
            .isEqualTo(
                listOf(
                    SkillGroupDto(1, "언어"), SkillGroupDto(2, "프레임워크")
                )
            )
    }

}