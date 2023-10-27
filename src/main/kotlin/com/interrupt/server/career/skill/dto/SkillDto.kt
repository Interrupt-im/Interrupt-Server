package com.interrupt.server.career.skill.dto

import com.interrupt.server.common.annotation.RedisEntity

@RedisEntity
data class SkillDto(
    val id: Long,
    val name: String,
    val skillGroup: SkillGroupDto
) {
}
