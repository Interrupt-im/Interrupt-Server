package com.interrupt.server.job.repository

import com.interrupt.server.job.entity.JobGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobGroupRepository: JpaRepository<JobGroup, Long> {
}