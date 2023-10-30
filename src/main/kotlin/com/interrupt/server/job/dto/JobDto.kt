package com.interrupt.server.job.dto

import com.interrupt.server.job.entity.Job
import com.interrupt.server.common.annotation.RedisEntity

@RedisEntity
data class JobDto(
    val id: Long,
    val name: String,
    val jobGroup: JobGroupDto
) {

    fun toEntity(): Job = Job(name, jobGroup.toEntity())

}
