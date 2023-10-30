package com.interrupt.server.skill.dto

import com.interrupt.server.member.dto.MemberDto
import com.interrupt.server.skill.entity.MemberSkill

data class MemberSkillDto(
    val member: MemberDto,
    val skill: SkillDto,
) {

    fun toEntity(): MemberSkill = MemberSkill(member.toEntity(), skill.toEntity())

}
