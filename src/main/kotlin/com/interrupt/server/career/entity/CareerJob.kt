package com.interrupt.server.career.entity

import com.interrupt.server.career.job.entity.Job
import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "career_job"
)
class CareerJob(
    @field:ManyToOne
    @field:JoinColumn(name = "career_id")
    val career: Career,
    @field:ManyToOne
    @field:JoinColumn(name = "job_id")
    val job: Job
): SoftDeleteBaseEntity() {
}