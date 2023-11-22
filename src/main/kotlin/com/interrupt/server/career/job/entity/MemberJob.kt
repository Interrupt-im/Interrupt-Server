package com.interrupt.server.career.job.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import com.interrupt.server.member.entity.Member
import jakarta.persistence.*

@Entity
@Table(
    name = "member_job",
)
class MemberJob(
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "member_id")
    val member: Member,
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "job_id")
    val job: Job,
): SoftDeleteBaseEntity() {

}