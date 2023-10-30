package com.interrupt.server.skill.dto

import com.interrupt.server.skill.entity.SkillGroup
import com.interrupt.server.common.annotation.RedisEntity

@RedisEntity
data class SkillGroupDto(
    val id: Long,
    val name: String,
) {

    val skillList: MutableList<SkillDto> = mutableListOf()

    fun toEntity(): SkillGroup = SkillGroup(name)

}
