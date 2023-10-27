package com.interrupt.server.career.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import com.interrupt.server.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(
    name = "career"
)
class Career(
    @field:OneToOne
    @field:JoinColumn(name = "member_id")
    val member: Member? = null,
    @field:OneToMany(mappedBy = "career")
    val jobList: MutableList<CareerJob> = mutableListOf(),
    @field:OneToMany(mappedBy = "career")
    val skillList: MutableList<CareerSkill> = mutableListOf(),
    @field:Column(name = "career_start_date", nullable = true)
    val careerStartDate: LocalDate? = null,
    @field:Column(name = "career_end_date", nullable = true)
    val careerEndDate: LocalDate? = null,
    @field:Column(name = "is_public")
    val isPublic: Boolean = true,
): SoftDeleteBaseEntity() {
}