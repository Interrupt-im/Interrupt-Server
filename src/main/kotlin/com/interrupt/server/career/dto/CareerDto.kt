package com.interrupt.server.career.dto

import com.interrupt.server.career.entity.Career
import com.interrupt.server.member.dto.MemberDto
import java.time.LocalDate

data class CareerDto(
    val id: Long? = null,
    val member: MemberDto,
    val title: String,
    val careerStartDate: LocalDate,
    val careerEndDate: LocalDate? = null,
    val isPublic: Boolean = true,
) {

    fun toEntity(): Career = Career(
        member = this.member.toEntity(),
        title = this.title,
        careerStartDate = this.careerStartDate,
        careerEndDate = this.careerEndDate,
        isPublic = this.isPublic
    )

    companion object {
        fun of(career: Career): CareerDto = CareerDto(career.id, MemberDto.of(career.member), career.title, career.careerStartDate, career.careerEndDate)
    }

}