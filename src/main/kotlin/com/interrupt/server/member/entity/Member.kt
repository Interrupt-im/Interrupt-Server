package com.interrupt.server.member.entity

import com.interrupt.server.common.entity.SoftDeleteBaseEntity
import com.interrupt.server.member.component.AesColumnEncryptor
import com.interrupt.server.member.dto.update.MemberUpdateRequest
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(
    name = "member",
    uniqueConstraints = [UniqueConstraint(name = "uk_member_login_id", columnNames = ["login_id"])]
)
class Member(
    @field:Column(name = "login_id", nullable = false, unique = true)
    @field:Convert(converter = AesColumnEncryptor::class)
    val loginId: String,
    @field:Column(name = "password", nullable = false)
    var loginPassword: String,
    @field:Column(name = "name", nullable = false)
    @field:Convert(converter = AesColumnEncryptor::class)
    var name: String,
    @field:Column(name = "email", nullable = false)
    @field:Convert(converter = AesColumnEncryptor::class)
    var email: String,
    @field:Column(name = "role", nullable = false)
    @field:Enumerated(EnumType.STRING)
    val role: MemberRole = MemberRole.USER
): SoftDeleteBaseEntity(), UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = this.role.getAuthorities()

    override fun getPassword(): String = this.loginPassword

    override fun getUsername(): String = this.loginId

    override fun isAccountNonExpired(): Boolean = this.deletedAt == null

    override fun isAccountNonLocked(): Boolean = this.deletedAt == null

    override fun isCredentialsNonExpired(): Boolean = this.deletedAt == null

    override fun isEnabled(): Boolean = this.deletedAt == null

    fun update(loginPassword: String? = null, name: String? = null, email: String? = null) {
        loginPassword?.let { this.loginPassword = it }
        name?.let { this.name = it }
        email?.let { this.email = it }
    }

    fun update(memberUpdateRequest: MemberUpdateRequest) {
        memberUpdateRequest.let { update(it.password, it.name, it.email) }
    }

}