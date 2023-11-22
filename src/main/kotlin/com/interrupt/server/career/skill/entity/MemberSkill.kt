package com.interrupt.server.career.skill.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import com.interrupt.server.member.entity.Member
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class MemberSkill(
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "member_id")
    val member: Member,
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "skill_id")
    val skill: Skill,
): SoftDeleteBaseEntity() {
}