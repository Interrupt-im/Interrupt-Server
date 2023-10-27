package com.interrupt.server.career.skill.repository

import com.interrupt.server.career.job.dto.JobGroupDto
import com.interrupt.server.career.skill.dto.SkillDto
import com.interrupt.server.career.skill.entity.Skill
import com.interrupt.server.career.skill.entity.SkillGroup
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