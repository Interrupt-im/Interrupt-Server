package com.interrupt.server.career.job.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.career.job.dto.JobDto
import com.interrupt.server.career.job.dto.JobGroupDto
import com.interrupt.server.career.job.dto.MemberJobDto
import com.interrupt.server.career.job.entity.Job
import com.interrupt.server.career.job.entity.JobGroup
import com.interrupt.server.career.job.repository.JobGroupRepository
import com.interrupt.server.career.job.repository.JobRepository
import com.interrupt.server.member.dto.MemberDto
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class JobServiceIntegrationTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var jobService: JobService
    @Autowired
    private lateinit var jobRepository: JobRepository
    @Autowired
    private lateinit var jobGroupRepository: JobGroupRepository
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `직무 리스트를 조회한다`() {
        // given
        val jobGroupName1 = "웹 개발"
        val jobGroupName2 = "게임 개발"
        val jobGroup1 = JobGroup(jobGroupName1)
        val jobGroup2 = JobGroup(jobGroupName2)

//        jobGroupRepository.saveAll(listOf(jobGroup1, jobGroup2))

        val jobName1 = "프론트엔드 개발자"
        val jobName2 = "백엔드 개발자"
        val jobName3 = "게임 클라이언트 개발 개발자"
        val job1 = Job(jobName1, jobGroup1)
        val job2 = Job(jobName2, jobGroup1)
        val job3 = Job(jobName3, jobGroup2)

//        jobRepository.saveAll(listOf(job1, job2, job3))

        val jobGroupDto1 = JobGroupDto(1, jobGroupName1)
        val jobGroupDto2 = JobGroupDto(2, jobGroupName2)

        val jobDto1 = JobDto(1, jobName1, jobGroupDto1)
        val jobDto2 = JobDto(2, jobName2, jobGroupDto1)

        // when
        val jobList = jobService.getJobList()

        // then
        assertThat(jobList)
            .hasSize(2)
            .isEqualTo(
                listOf(
                    jobGroupDto1,
                    jobGroupDto2,
                )
            )
        assertThat(jobList[0].jobList)
            .hasSize(2)
            .isEqualTo(
                listOf(
                    jobDto1,
                    jobDto2,
                )
            )
    }

    @Test
    fun `회원 별 기술을 등록한다`() {
        // given
        val member = memberRepository.save(Member("memberId", "password", "name", "email@domain.com"))
        val memberDto = MemberDto.of(member)

        val jobGroupName1 = "웹 개발"
        val jobGroupName2 = "게임 개발"
        val jobGroup1 = JobGroup(jobGroupName1)
        val jobGroup2 = JobGroup(jobGroupName2)

//        jobGroupRepository.saveAll(listOf(jobGroup1, jobGroup2))

        val jobName1 = "프론트엔드 개발자"
        val jobName2 = "백엔드 개발자"
        val jobName3 = "게임 클라이언트 개발 개발자"
        val job1 = Job(jobName1, jobGroup1)
        val job2 = Job(jobName2, jobGroup1)
        val job3 = Job(jobName3, jobGroup2)

//        jobRepository.saveAll(listOf(job1, job2, job3))

        val jobGroupDto1 = JobGroupDto(1, jobGroupName1)
        val jobGroupDto2 = JobGroupDto(2, jobGroupName2)

        val jobDto1 = JobDto(1, jobName1, jobGroupDto1)
        val jobDto2 = JobDto(2, jobName2, jobGroupDto1)

        // when then
        jobService.registerMemberJob(listOf(MemberJobDto(memberDto, jobDto1), MemberJobDto(memberDto, jobDto2)))
    }

}