package com.interrupt.server.career.job.repository

import com.interrupt.server.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class JobQueryRepositoryTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var jobQueryRepository: JobQueryRepository

    @Test
    fun `직군과 직무 리스트를 DTO 형태로 변환하여 조회한다`() {
        // given when
        val jobList = jobQueryRepository.findJobDtoList()

        // then
        assertThat(jobList).isNotEmpty
    }

}