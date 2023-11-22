package com.interrupt.server.member.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "member",
    uniqueConstraints = [UniqueConstraint(name = "uk_member_login_id", columnNames = ["login_id"])]
)
class Member(
    @field:Column(name = "login_id", nullable = false, unique = true)
    var loginId: String,
    @field:Column(name = "password", nullable = false)
    var password: String,
    @field:Column(name = "name", nullable = false)
    var name: String,
    @field:Column(name = "email", nullable = false)
    var email: String
): SoftDeleteBaseEntity() {

}