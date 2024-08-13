package com.interrupt.server.member.infra

import com.interrupt.server.member.domain.Member
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository : JpaRepository<Member, Long>, KotlinJdslJpqlExecutor {
}
