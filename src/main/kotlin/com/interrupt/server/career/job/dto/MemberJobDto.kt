package com.interrupt.server.career.job.dto

import com.interrupt.server.career.job.entity.MemberJob
import com.interrupt.server.member.dto.MemberDto

data class MemberJobDto(
    val member: MemberDto,
    val job: JobDto,
) {

    fun toEntity(): MemberJob = MemberJob(member.toEntity(), job.toEntity())

}