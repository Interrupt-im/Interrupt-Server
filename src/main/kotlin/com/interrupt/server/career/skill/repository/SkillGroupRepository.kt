package com.interrupt.server.career.skill.repository

import com.interrupt.server.career.skill.entity.SkillGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillGroupRepository: JpaRepository<SkillGroup, Long> {
}