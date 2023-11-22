package com.interrupt.server.career.skill.service

import com.interrupt.server.IntegrationTestSupport
import com.interrupt.server.career.skill.dto.MemberSkillDto
import com.interrupt.server.career.skill.dto.SkillDto
import com.interrupt.server.career.skill.dto.SkillGroupDto
import com.interrupt.server.career.skill.entity.Skill
import com.interrupt.server.career.skill.entity.SkillGroup
import com.interrupt.server.career.skill.repository.SkillGroupRepository
import com.interrupt.server.career.skill.repository.SkillRepository
import com.interrupt.server.member.dto.MemberDto
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SkillServiceIntegrationTest: IntegrationTestSupport() {

    @Autowired
    private lateinit var skillService: SkillService
    @Autowired
    private lateinit var skillRepository: SkillRepository
    @Autowired
    private lateinit var skillGroupRepository: SkillGroupRepository
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `기술 리스트를 조회한다`() {
        // given
        val skillGroupName1 = "언어"
        val skillGroupName2 = "프레임워크"
        val skillGroup1 = SkillGroup(skillGroupName1)
        val skillGroup2 = SkillGroup(skillGroupName2)

//        skillGroupRepository.saveAll(listOf(skillGroup1, skillGroup2))

        val skillName1 = "Java"
        val skillName2 = "Kotlin"
        val skillName3 = "JavaScript"
        val skillName4 = "TypeScript"
        val skillName5 = "Python"
        val skillName6 = "C"
        val skillName7 = "C++"
        val skillName8 = "C#"
        val skillName9 = "Spring"
        val skillName10 = "Nest"
        val skillName11 = "Next"
        val skillName12 = "React"
        val skillName13 = "Vue"
        val skillName14 = "Angular"
        val skill1 = Skill(skillName1, skillGroup1)
        val skill2 = Skill(skillName2, skillGroup1)
        val skill3 = Skill(skillName3, skillGroup1)
        val skill4 = Skill(skillName4, skillGroup1)
        val skill5 = Skill(skillName5, skillGroup1)
        val skill6 = Skill(skillName6, skillGroup1)
        val skill7 = Skill(skillName7, skillGroup1)
        val skill8 = Skill(skillName8, skillGroup1)
        val skill9 = Skill(skillName9, skillGroup2)
        val skill10 = Skill(skillName10, skillGroup2)
        val skill11 = Skill(skillName11, skillGroup2)
        val skill12 = Skill(skillName12, skillGroup2)
        val skill13 = Skill(skillName13, skillGroup2)
        val skill14 = Skill(skillName14, skillGroup2)

//        skillRepository.saveAll(listOf(
//            skill1,
//            skill2,
//            skill3,
//            skill4,
//            skill5,
//            skill6,
//            skill7,
//            skill8,
//            skill9,
//            skill10,
//            skill11,
//            skill12,
//            skill13,
//            skill14))

        val skillGroupDto1 = SkillGroupDto(1, skillGroupName1)
        val skillGroupDto2 = SkillGroupDto(2, skillGroupName2)

        val skillDto1 = SkillDto(1, skillName1, skillGroupDto1)
        val skillDto2 = SkillDto(2, skillName2, skillGroupDto1)
        val skillDto3 = SkillDto(3, skillName3, skillGroupDto1)
        val skillDto4 = SkillDto(4, skillName4, skillGroupDto1)
        val skillDto5 = SkillDto(5, skillName5, skillGroupDto1)
        val skillDto6 = SkillDto(6, skillName6, skillGroupDto1)
        val skillDto7 = SkillDto(7, skillName7, skillGroupDto1)
        val skillDto8 = SkillDto(8, skillName8, skillGroupDto1)
        val skillDto9 = SkillDto(9, skillName9, skillGroupDto2)
        val skillDto10 = SkillDto(10, skillName10, skillGroupDto2)
        val skillDto11 = SkillDto(11, skillName11, skillGroupDto2)
        val skillDto12 = SkillDto(12, skillName12, skillGroupDto2)
        val skillDto13 = SkillDto(13, skillName13, skillGroupDto2)
        val skillDto14 = SkillDto(14, skillName14, skillGroupDto2)

        // when
        val skillList = skillService.getSkillList()

        // then
        assertThat(skillList)
            .hasSize(2)
            .isEqualTo(
                listOf(
                    skillGroupDto1,
                    skillGroupDto2,
                )
            )
        assertThat(skillList[0].skillList)
            .hasSize(8)
            .isEqualTo(
                listOf(
                    skillDto1,
                    skillDto2,
                    skillDto3,
                    skillDto4,
                    skillDto5,
                    skillDto6,
                    skillDto7,
                    skillDto8,
                )
            )
    }

    @Test
    fun `회원 별 기술을 등록한다`() {
        // given
        val member = memberRepository.save(Member("memberId", "password", "name", "email@domain.com"))
        val memberDto = MemberDto.of(member)

        val skillGroupName1 = "언어"
        val skillGroupName2 = "프레임워크"
        val skillGroup1 = SkillGroup(skillGroupName1)
        val skillGroup2 = SkillGroup(skillGroupName2)

//        skillGroupRepository.saveAll(listOf(skillGroup1, skillGroup2))

        val skillName1 = "Java"
        val skillName2 = "Kotlin"
        val skillName3 = "JavaScript"
        val skillName4 = "TypeScript"
        val skillName5 = "Python"
        val skillName6 = "C"
        val skillName7 = "C++"
        val skillName8 = "C#"
        val skillName9 = "Spring"
        val skillName10 = "Nest"
        val skillName11 = "Next"
        val skillName12 = "React"
        val skillName13 = "Vue"
        val skillName14 = "Angular"
        val skill1 = Skill(skillName1, skillGroup1)
        val skill2 = Skill(skillName2, skillGroup1)
        val skill3 = Skill(skillName3, skillGroup1)
        val skill4 = Skill(skillName4, skillGroup1)
        val skill5 = Skill(skillName5, skillGroup1)
        val skill6 = Skill(skillName6, skillGroup1)
        val skill7 = Skill(skillName7, skillGroup1)
        val skill8 = Skill(skillName8, skillGroup1)
        val skill9 = Skill(skillName9, skillGroup2)
        val skill10 = Skill(skillName10, skillGroup2)
        val skill11 = Skill(skillName11, skillGroup2)
        val skill12 = Skill(skillName12, skillGroup2)
        val skill13 = Skill(skillName13, skillGroup2)
        val skill14 = Skill(skillName14, skillGroup2)

//        skillRepository.saveAll(listOf(
//            skill1,
//            skill2,
//            skill3,
//            skill4,
//            skill5,
//            skill6,
//            skill7,
//            skill8,
//            skill9,
//            skill10,
//            skill11,
//            skill12,
//            skill13,
//            skill14))

        val skillGroupDto1 = SkillGroupDto(1, skillGroupName1)
        val skillGroupDto2 = SkillGroupDto(2, skillGroupName2)

        val skillDto1 = SkillDto(1, skillName1, skillGroupDto1)
        val skillDto2 = SkillDto(2, skillName2, skillGroupDto1)
        val skillDto3 = SkillDto(3, skillName3, skillGroupDto1)
        val skillDto4 = SkillDto(4, skillName4, skillGroupDto1)
        val skillDto5 = SkillDto(5, skillName5, skillGroupDto1)
        val skillDto6 = SkillDto(6, skillName6, skillGroupDto1)
        val skillDto7 = SkillDto(7, skillName7, skillGroupDto1)
        val skillDto8 = SkillDto(8, skillName8, skillGroupDto1)
        val skillDto9 = SkillDto(9, skillName9, skillGroupDto2)
        val skillDto10 = SkillDto(10, skillName10, skillGroupDto2)
        val skillDto11 = SkillDto(11, skillName11, skillGroupDto2)
        val skillDto12 = SkillDto(12, skillName12, skillGroupDto2)
        val skillDto13 = SkillDto(13, skillName13, skillGroupDto2)
        val skillDto14 = SkillDto(14, skillName14, skillGroupDto2)

        // when then
        skillService.registerMemberSkill(listOf(MemberSkillDto(memberDto, skillDto1), MemberSkillDto(memberDto, skillDto9)))
    }

}