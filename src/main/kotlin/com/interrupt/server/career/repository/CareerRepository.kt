package com.interrupt.server.career.repository

import com.interrupt.server.career.entity.Career
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface CareerRepository: JpaRepository<Career, Long>, KotlinJdslJpqlExecutor {
}