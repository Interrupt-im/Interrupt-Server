package com.interrupt.server.skill.service

import com.interrupt.server.member.repository.MemberSkillRepository
import com.interrupt.server.skill.dto.MemberSkillDto
import com.interrupt.server.skill.dto.SkillDto
import com.interrupt.server.skill.dto.SkillGroupDto
import com.interrupt.server.skill.repository.SkillQueryRepository
import com.interrupt.server.skill.repository.SkillGroupRedisRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SkillService(
    private val skillQueryRepository: SkillQueryRepository,
    private val skillGroupRedisRepository: SkillGroupRedisRepository,
    private val memberSkillRepository: MemberSkillRepository
) {

    /**
     * 기술 리스트 조회
     */
    fun getSkillList(): List<SkillGroupDto> {
        val skillGroupList = skillGroupRedisRepository.findAll()

        if (skillGroupList.isEmpty()) {
            return skillQueryRepository.findJobDtoList()
                .groupBy(SkillDto::skillGroup)
                .map { (skillGroupDto, skillDtoList) ->
                    SkillGroupDto(skillGroupDto.id, skillGroupDto.name)
                        .apply { skillList.addAll(skillDtoList) }
                }.also { skillGroupRedisRepository.saveAll(it) }
        }

        return skillGroupList
    }

    /**
     * 회원 별 기술 등록
     */
    fun registerMemberSkill(memberSkillDtos: List<MemberSkillDto>) {
        val entities = memberSkillDtos.map { it.toEntity() }
        memberSkillRepository.saveAll(entities)
    }

}