package com.interrupt.server.job.repository

import com.interrupt.server.job.entity.Job
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository: JpaRepository<Job, Long>, KotlinJdslJpqlExecutor {
}