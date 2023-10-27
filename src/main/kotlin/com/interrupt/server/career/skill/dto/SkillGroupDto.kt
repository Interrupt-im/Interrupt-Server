package com.interrupt.server.career.skill.dto

import com.interrupt.server.common.annotation.RedisEntity

@RedisEntity
data class SkillGroupDto(
    val id: Long,
    val name: String,
) {
    val skillList: MutableList<SkillDto> = mutableListOf()
}
