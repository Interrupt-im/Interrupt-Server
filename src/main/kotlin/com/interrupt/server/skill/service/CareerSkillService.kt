package com.interrupt.server.skill.service

import com.interrupt.server.skill.dto.SkillDto
import com.interrupt.server.skill.dto.SkillGroupDto
import com.interrupt.server.skill.repository.CareerSkillQueryRepository
import com.interrupt.server.skill.repository.SkillGroupRedisRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CareerSkillService(
    private val careerSkillQueryRepository: CareerSkillQueryRepository,
    private val skillGroupRedisRepository: SkillGroupRedisRepository
) {

    /**
     * 기술 리스트 조회
     */
    fun getSkillList(): Any {
        val skillGroupList = skillGroupRedisRepository.findAll()

        if (skillGroupList.isEmpty()) {
            return careerSkillQueryRepository.findJobDtoList()
                .groupBy(SkillDto::skillGroup)
                .map { (skillGroupDto, skillDtoList) ->
                    SkillGroupDto(skillGroupDto.id, skillGroupDto.name)
                        .apply { skillList.addAll(skillDtoList) }
                }.also { skillGroupRedisRepository.saveAll(it) }
        }

        return skillGroupList
    }

}