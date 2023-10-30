package com.interrupt.server.career.dto

import com.interrupt.server.career.entity.Career
import com.interrupt.server.skill.dto.SkillDto
import com.interrupt.server.member.dto.MemberDto
import java.time.LocalDate

data class CareerDto(
    val id: Long? = null,
    val member: MemberDto,
    val jobList: MutableList<CareerJobDto> = mutableListOf(),
    val skillList: MutableList<SkillDto> = mutableListOf(),
    val careerStartDate: LocalDate,
    val careerEndDate: LocalDate,
    val isPublic: Boolean = true,
) {

    fun toEntity(): Career = Career(
        member = this.member.toEntity(),
        careerStartDate = this.careerStartDate,
        careerEndDate = this.careerEndDate,
        isPublic = this.isPublic
    )

    companion object {
    }

}