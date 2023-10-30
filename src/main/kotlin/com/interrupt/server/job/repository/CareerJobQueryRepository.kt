package com.interrupt.server.job.repository

import com.interrupt.server.job.dto.JobDto
import com.interrupt.server.job.dto.JobGroupDto
import com.interrupt.server.job.entity.Job
import com.interrupt.server.job.entity.JobGroup
import org.springframework.stereotype.Repository

@Repository
class CareerJobQueryRepository(
   private val careerJobRepository: CareerJobRepository
) {

    fun findJobDtoList(): List<JobDto> = careerJobRepository.findAll {
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
    }.filterNotNull()

}