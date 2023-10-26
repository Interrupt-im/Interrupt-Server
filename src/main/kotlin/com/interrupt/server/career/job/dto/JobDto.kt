package com.interrupt.server.career.job.dto

data class JobDto(
    val id: Long,
    val name: String,
    val jobGroup: JobGroupDto
) {
}
