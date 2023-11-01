package com.interrupt.server.career.job.service

import com.interrupt.server.career.job.dto.JobDto
import com.interrupt.server.career.job.dto.JobGroupDto
import com.interrupt.server.career.job.dto.MemberJobDto
import com.interrupt.server.career.job.repository.JobQueryRepository
import com.interrupt.server.career.job.repository.JobGroupRedisRepository
import com.interrupt.server.career.job.repository.MemberJobRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class JobService(
    private val jobQueryRepository: JobQueryRepository,
    private val jobGroupRedisRepository: JobGroupRedisRepository,
    private val memberJobRepository: MemberJobRepository,
) {

    /**
     * 직무 리스트 조회
     */
    fun getJobList(): List<JobGroupDto> {
        val jobGroupList = jobGroupRedisRepository.findAll()

        if (jobGroupList.isEmpty()) {
            return jobQueryRepository.findJobDtoList()
                .groupBy(JobDto::jobGroup)
                .map { (jobGroupDto, jobDtoList) ->
                    JobGroupDto(jobGroupDto.id, jobGroupDto.name)
                        .apply { jobList.addAll(jobDtoList) }
                }.also { jobGroupRedisRepository.saveAll(it) }
        }

        return jobGroupList
    }

    /**
     * 회원 별 기술 등록
     */
    fun registerMemberJob(memberJobDtos: List<MemberJobDto>) {
        val entities = memberJobDtos.map { it.toEntity() }
        memberJobRepository.saveAll(entities)
    }

}