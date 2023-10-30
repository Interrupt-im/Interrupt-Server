package com.interrupt.server.skill.repository

import com.interrupt.server.skill.entity.Skill
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillRepository : JpaRepository<Skill, Long>, KotlinJdslJpqlExecutor {
}