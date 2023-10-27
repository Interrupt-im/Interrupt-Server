package com.interrupt.server.career.job.dto

import com.interrupt.server.common.annotation.RedisEntity

@RedisEntity
data class JobGroupDto(
    val id: Long,
    val name: String,
) {
    val jobList: MutableList<JobDto> = mutableListOf()
}
