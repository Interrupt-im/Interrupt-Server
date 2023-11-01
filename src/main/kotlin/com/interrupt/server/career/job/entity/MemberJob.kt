package com.interrupt.server.career.job.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import com.interrupt.server.member.entity.Member
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class MemberJob(
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "member_id")
    val member: Member,
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "job_id")
    val job: Job,
): SoftDeleteBaseEntity() {

}