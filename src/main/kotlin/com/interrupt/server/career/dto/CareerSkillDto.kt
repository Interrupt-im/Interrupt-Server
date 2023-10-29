package com.interrupt.server.career.dto

import com.interrupt.server.career.entity.CareerSkill
import com.interrupt.server.career.skill.dto.SkillDto


class CareerSkillDto(
    val career: CareerDto,
    val skill: SkillDto
) {

    fun toEntity(): CareerSkill = CareerSkill(career.toEntity(), skill.toEntity())

}