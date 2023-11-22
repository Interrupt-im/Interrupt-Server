package com.interrupt.server.common.entity

import jakarta.persistence.Column
import org.hibernate.annotations.Where
import java.time.LocalDateTime

@Where(clause = "deleted_at is NULL")
abstract class SoftDeleteBaseEntity: BaseEntity() {

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null
}