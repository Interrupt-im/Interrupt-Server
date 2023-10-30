package com.interrupt.server.member.repository

import com.interrupt.server.skill.entity.MemberSkill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberSkillRepository: JpaRepository<MemberSkill, Long> {
}