package com.interrupt.server.skill.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "skill_group",
    uniqueConstraints = [UniqueConstraint(name = "uk_skill_group_name", columnNames = ["name"])]
)
class SkillGroup(
    @field:Column(name = "name", nullable = false, unique = true)
    var name :String,
): SoftDeleteBaseEntity() {
}