package com.interrupt.server.career.job.service

import com.interrupt.server.career.job.dto.JobDto
import com.interrupt.server.career.job.dto.JobGroupDto
import com.interrupt.server.career.job.entity.Job
import com.interrupt.server.career.job.entity.JobGroup
import com.interrupt.server.career.job.repository.CareerJobGroupRepository
import com.interrupt.server.career.job.repository.CareerJobRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CareerJobService(
    private val jobGroupRepository: CareerJobGroupRepository,
    private val jobRepository: CareerJobRepository
) {

    /**
     * 직무 리스트 조회
     */
    fun getJobList(): Any {
        return jobRepository.findAll {
            select(
                new(
                    JobDto::class,
                    path(Job::id),
                    path(Job::name),
                    new(
                        JobGroupDto::class,
                        path(JobGroup::id),
                        path(JobGroup::name),
                    )
                )
            ).from(
                entity(Job::class),
                join(Job::jobGroup).`as`(entity(JobGroup::class))
            )
        }
            .filterNotNull()
            .groupBy(JobDto::jobGroup)
            .map { (jobGroupDto, jobDtoList) ->
                JobGroupDto(jobGroupDto.id, jobGroupDto.name)
                    .apply { jobList.addAll(jobDtoList) }
            }
    }

}