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
    val member: Member,
    @field:Column(name = "title")
    val title: String,
    @field:Column(name = "career_start_date")
    @field:Temporal(TemporalType.DATE)
    val careerStartDate: LocalDate,
    @field:Column(name = "career_end_date", nullable = true)
    @field:Temporal(TemporalType.DATE)
    val careerEndDate: LocalDate? = null,
    @field:Column(name = "is_public")
    val isPublic: Boolean = true,
): SoftDeleteBaseEntity() {
}