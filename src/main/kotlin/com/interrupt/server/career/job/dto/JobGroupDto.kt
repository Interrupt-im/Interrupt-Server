package com.interrupt.server.career.job.dto

data class JobGroupDto(
    val id: Long,
    val name: String,
) {
    val jobList: MutableList<JobDto> = mutableListOf()
}
