package com.interrupt.server.skill.dto

import com.interrupt.server.skill.entity.Skill
import com.interrupt.server.common.annotation.RedisEntity

@RedisEntity
data class SkillDto(
    val id: Long,
    val name: String,
    val skillGroup: SkillGroupDto
) {

    fun toEntity(): Skill = Skill(name, skillGroup.toEntity())

}
