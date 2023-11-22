package com.interrupt.server.career.job.repository

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.career.job.dto.JobGroupDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class JobGroupRedisRepositoryTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var jobGroupRedisRepository: JobGroupRedisRepository

    @Test
    fun `직군을 저장한다`() {
        // given
        val jobGroupDto = JobGroupDto(1, "웹 개발")

        // when then
        jobGroupRedisRepository.save(jobGroupDto)
    }

    @Test
    fun `직군 리스트를 저장한다`() {
        // given
        val jobGroupDto1 = JobGroupDto(1, "웹 개발")
        val jobGroupDto2 = JobGroupDto(2, "게임 개발")

        // when then
        jobGroupRedisRepository.saveAll(listOf(jobGroupDto1, jobGroupDto2))
    }

    @Test
    fun `저장된 직군 리스트를 반환한다`() {
        // given
        val jobGroupDto1 = JobGroupDto(1, "웹 개발")
        val jobGroupDto2 = JobGroupDto(2, "게임 개발")
        jobGroupRedisRepository.saveAll(listOf(jobGroupDto1, jobGroupDto2))

        // when
        val jobGroupDtos = jobGroupRedisRepository.findAll()

        // then
        assertThat(jobGroupDtos)
            .hasSize(2)
            .isEqualTo(
                listOf(
                    JobGroupDto(1, "웹 개발"), JobGroupDto(2, "게임 개발")
                )
            )
    }

}