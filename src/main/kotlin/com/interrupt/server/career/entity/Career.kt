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
    var title: String,
    @field:Column(name = "career_start_date")
    @field:Temporal(TemporalType.DATE)
    var careerStartDate: LocalDate,
    @field:Column(name = "career_end_date", nullable = true)
    @field:Temporal(TemporalType.DATE)
    var careerEndDate: LocalDate? = null,
    @field:Column(name = "is_public")
    var isPublic: Boolean = true,
): SoftDeleteBaseEntity() {

    fun update(title: String? = null, careerStartDate: LocalDate? = null, careerEndDate: LocalDate? = null, isPublic: Boolean? = null) {
        title?.let { this.title = it }
        careerStartDate?.let { this.careerStartDate = it }
        careerEndDate?.let { this.careerEndDate = it }
        isPublic?.let { this.isPublic = it }
    }

}