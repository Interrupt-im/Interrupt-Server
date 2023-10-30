package com.interrupt.server.career.entity

import com.interrupt.server.skill.entity.Skill
import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "career_skill"
)
class CareerSkill(
    @field:ManyToOne
    @field:JoinColumn(name = "career_id")
    val career: Career,
    @field:ManyToOne
    @field:JoinColumn(name = "skill_id")
    val skill: Skill
): SoftDeleteBaseEntity() {
}