package com.interrupt.server.user.domain

import com.interrupt.server.global.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "member",
    uniqueConstraints = [UniqueConstraint(name = "uk_member_email", columnNames = ["email"])]
)
class Member(
    email: String,
    loginPassword: String,
    nickname: String,
    memberType: MemberType = MemberType.CUSTOMER
): BaseEntity() {
    @field:Column(name = "email", nullable = false, unique = true)
    var email: String = email
        protected set

    @field:Column(name = "password", nullable = false)
    var loginPassword: String = loginPassword
        protected set

    @field:Column(name = "nickname", nullable = false)
    var nickname: String = nickname
        protected set

    @field:Column(name = "account_type", nullable = false)
    @field:Enumerated(EnumType.STRING)
    var memberType: MemberType = memberType
        protected set
}
