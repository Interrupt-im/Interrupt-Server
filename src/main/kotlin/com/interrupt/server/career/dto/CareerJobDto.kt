package com.interrupt.server.career.dto

import com.interrupt.server.career.entity.CareerJob
import com.interrupt.server.job.dto.JobDto

data class CareerJobDto(
    val career: CareerDto,
    val job: JobDto
) {

    fun toEntity(): CareerJob = CareerJob(career.toEntity(), job.toEntity())

}