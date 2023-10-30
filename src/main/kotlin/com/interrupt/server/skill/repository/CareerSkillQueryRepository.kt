package com.interrupt.server.skill.repository

import com.interrupt.server.job.dto.JobGroupDto
import com.interrupt.server.skill.dto.SkillDto
import com.interrupt.server.skill.entity.Skill
import com.interrupt.server.skill.entity.SkillGroup
import org.springframework.stereotype.Repository

@Repository
class CareerSkillQueryRepository(
   private val careerSkillRepository: CareerSkillRepository
) {

    fun findJobDtoList(): List<SkillDto> = careerSkillRepository.findAll {
        select(
            new(
                SkillDto::class,
                path(Skill::id),
                path(Skill::name),
                new(
                    JobGroupDto::class,
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