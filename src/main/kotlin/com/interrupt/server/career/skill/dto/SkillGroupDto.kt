package com.interrupt.server.career.skill.dto

import com.interrupt.server.career.skill.entity.SkillGroup
import com.interrupt.server.common.redis.RedisEntity

@RedisEntity
data class SkillGroupDto(
    val id: Long,
    val name: String,
) {

    val skillList: MutableList<SkillDto> = mutableListOf()

    fun toEntity(): SkillGroup = SkillGroup(name)

}
