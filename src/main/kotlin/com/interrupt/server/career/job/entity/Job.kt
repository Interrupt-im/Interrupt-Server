package com.interrupt.server.career.job.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "job",
    uniqueConstraints = [UniqueConstraint(name = "uk_job_name", columnNames = ["name"])]
)
class Job(
    @field:Column(name = "name", nullable = false, unique = true)
    var name: String,
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "job_group_id")
    var jobGroup: JobGroup
): SoftDeleteBaseEntity() {
}