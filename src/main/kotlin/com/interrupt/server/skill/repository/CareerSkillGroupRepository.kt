package com.interrupt.server.skill.repository

import com.interrupt.server.skill.entity.SkillGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CareerSkillGroupRepository: JpaRepository<SkillGroup, Long> {
}