package com.interrupt.server.career.job.service

import com.interrupt.server.career.job.dto.JobDto
import com.interrupt.server.career.job.dto.JobGroupDto
import com.interrupt.server.career.job.dto.MemberJobDto
import com.interrupt.server.career.job.entity.MemberJob
import com.interrupt.server.career.job.repository.JobGroupRedisRepository
import com.interrupt.server.career.job.repository.JobQueryRepository
import com.interrupt.server.career.job.repository.MemberJobRepository
import com.interrupt.server.member.dto.MemberDto
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class JobServiceTest {

    companion object {

        private val jobQueryRepository: JobQueryRepository = mockk()
        private val jobGroupRedisRepository: JobGroupRedisRepository = mockk()
        private val memberJobRepository: MemberJobRepository = mockk()
        private val jobService: JobService = JobService(jobQueryRepository, jobGroupRedisRepository, memberJobRepository)

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
    fun `직무 리스트 조회 시 레디스 저장소에 캐싱된 리스트가 있다면 캐싱 된 리스트를 반환한다`() {
        // given
        every { jobGroupRedisRepository.findAll() } returns listOf(JobGroupDto(1, "웹 개발"), JobGroupDto(2, "게임 개발"))

        // when
        val jobList = jobService.getJobList()

        // then
        assertThat(jobList)
            .hasSize(2)
            .isEqualTo(
                listOf(JobGroupDto(1, "웹 개발"), JobGroupDto(2, "게임 개발"))
            )
    }

    @Test
    fun `직무 리스트 조회 시 레디스 저장소에 캐싱된 리스트가 없다면 DB 에서 조회해 레디스 저장소에 캐싱 후 반환한다`() {
        // given
        val jobGroupDto1 = JobGroupDto(1, "웹 개발")
        val jobGroupDto2 = JobGroupDto(2, "게임 개발")

        every { jobGroupRedisRepository.findAll() } returns listOf()
        every { jobQueryRepository.findJobDtoList() } returns listOf(
            JobDto(1, "프론트엔드", jobGroupDto1), JobDto(2, "백엔드", jobGroupDto1),
            JobDto(1, "게임 클라이언트 개발", jobGroupDto2)
        )
        justRun { jobGroupRedisRepository.saveAll(any<List<JobGroupDto>>()) }

        // when
        val jobList = jobService.getJobList()

        // then
        assertThat(jobList)
            .hasSize(2)
            .isEqualTo(
                listOf(JobGroupDto(1, "웹 개발"), JobGroupDto(2, "게임 개발"))
            )
    }

    @Test
    fun `회원별 직무 리스트를 저장한다`() {
        // given
        val jobGroupDto1 = JobGroupDto(1, "웹 개발")
        val jobGroupDto2 = JobGroupDto(2, "게임 개발")

        val jobDto1 = JobDto(1, "프론트엔드", jobGroupDto1)
        val jobDto2 = JobDto(2, "백엔드", jobGroupDto1)
        val jobDto3 = JobDto(1, "게임 클라이언트 개발", jobGroupDto2)

        val member = MemberDto(1, "memberId", "password", "name", "email@domain.com")

        val memberJobDto1 = MemberJobDto(member, jobDto1)
        val memberJobDto2 = MemberJobDto(member, jobDto2)
        val memberJobDto3 = MemberJobDto(member, jobDto3)

        every { memberJobRepository.saveAll(any<List<MemberJob>>()) } returns listOf()

        // when then
        jobService.registerMemberJob(listOf(memberJobDto1, memberJobDto2, memberJobDto3))
    }
}