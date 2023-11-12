package com.interrupt.server.career.skill.repository

import com.interrupt.server.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SkillQueryRepositoryTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var skillQueryRepository: SkillQueryRepository

    @Test
    fun `기술 그룹과 기술 리스트를 DTO 형태로 변환하여 조회한다`() {
        // given when
        val skillList = skillQueryRepository.findSkillDtoList()

        // then
        assertThat(skillList).isNotEmpty
    }

}