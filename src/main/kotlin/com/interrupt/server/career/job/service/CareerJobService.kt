package com.interrupt.server.career.job.service

import com.interrupt.server.career.job.dto.JobDto
import com.interrupt.server.career.job.dto.JobGroupDto
import com.interrupt.server.career.job.repository.CareerJobQueryRepository
import com.interrupt.server.career.job.repository.JobGroupRedisRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CareerJobService(
    private val careerJobQueryRepository: CareerJobQueryRepository,
    private val jobGroupRedisRepository: JobGroupRedisRepository
) {

    /**
     * 직무 리스트 조회
     */
    fun getJobList(): Any {
        val jobGroupList = jobGroupRedisRepository.findAll()

        if (jobGroupList.isEmpty()) {
            return careerJobQueryRepository.findJobDtoList()
                .groupBy(JobDto::jobGroup)
                .map { (jobGroupDto, jobDtoList) ->
                    JobGroupDto(jobGroupDto.id, jobGroupDto.name)
                        .apply { jobList.addAll(jobDtoList) }
                }.also { jobGroupRedisRepository.saveAll(it) }
        }

        return jobGroupList
    }

}