package com.interrupt.server.career.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import com.interrupt.server.member.entity.Member
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "career"
)
class Career(
    @field:ManyToOne
    @field:JoinColumn(name = "member_id")
    val member: Member? = null,
    @field:Column(name = "title")
    val title: String,
    @field:Column(name = "career_start_date")
    val careerStartDate: LocalDate,
    @field:Column(name = "career_end_date", nullable = true)
    val careerEndDate: LocalDate? = null,
    @field:Column(name = "is_public")
    val isPublic: Boolean = true,
): SoftDeleteBaseEntity() {
}