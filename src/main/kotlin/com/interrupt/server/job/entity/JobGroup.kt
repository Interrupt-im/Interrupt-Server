package com.interrupt.server.job.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "job_group",
    uniqueConstraints = [UniqueConstraint(name = "uk_job_group_name", columnNames = ["name"])]
)
class JobGroup(
    @field:Column(name = "name", nullable = false, unique = true)
    var name :String,
): SoftDeleteBaseEntity() {
}