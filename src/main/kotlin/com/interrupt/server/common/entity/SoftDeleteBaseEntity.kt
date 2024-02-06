package com.interrupt.server.common.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class SoftDeleteBaseEntity: BaseEntity() {

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null

    fun delete() {
        deletedAt = LocalDateTime.now()
    }

    fun recoverDeleteStatus() {
        deletedAt = null
    }

}