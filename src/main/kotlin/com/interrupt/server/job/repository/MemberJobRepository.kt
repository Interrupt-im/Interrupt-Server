package com.interrupt.server.job.repository

import com.interrupt.server.job.entity.MemberJob
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberJobRepository: JpaRepository<MemberJob, Long> {
}