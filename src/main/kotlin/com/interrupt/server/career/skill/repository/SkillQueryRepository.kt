package com.interrupt.server.career.skill.repository

import com.interrupt.server.career.skill.dto.SkillDto
import com.interrupt.server.career.skill.dto.SkillGroupDto
import com.interrupt.server.career.skill.entity.Skill
import com.interrupt.server.career.skill.entity.SkillGroup
import org.springframework.stereotype.Repository

@Repository
class SkillQueryRepository(
   private val skillRepository: SkillRepository
) {

    fun findSkillDtoList(): List<SkillDto> = skillRepository.findAll {
        select(
            new(
                SkillDto::class,
                path(Skill::id),
                path(Skill::name),
                new(
                    SkillGroupDto::class,
                    path(SkillGroup::id),
                    path(SkillGroup::name),
                )
            )
        ).from(
            entity(Skill::class),
            join(Skill::skillGroup).`as`(entity(SkillGroup::class))
        )
    }.filterNotNull()

}