package com.interrupt.server.career.skill.repository

import com.interrupt.server.career.skill.entity.Skill
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CareerSkillRepository : JpaRepository<Skill, Long>, KotlinJdslJpqlExecutor {
}