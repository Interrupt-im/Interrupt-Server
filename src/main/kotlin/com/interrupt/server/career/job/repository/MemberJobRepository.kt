package com.interrupt.server.career.job.repository

import com.interrupt.server.career.job.entity.MemberJob
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberJobRepository: JpaRepository<MemberJob, Long> {
}