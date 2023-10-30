package com.interrupt.server.skill.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "skill",
    uniqueConstraints = [UniqueConstraint(name = "uk_skill_name", columnNames = ["name"])]
)
class Skill(
    @field:Column(name = "name", nullable = false, unique = true)
    var name: String,
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "skill_group_id", nullable = false)
    var skillGroup: SkillGroup
): SoftDeleteBaseEntity() {
}