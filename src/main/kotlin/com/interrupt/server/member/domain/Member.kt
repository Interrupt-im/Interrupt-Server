package com.interrupt.server.member.domain

import com.interrupt.server.global.common.BaseEntity
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode
import jakarta.persistence.*

@Entity
@Table(
    name = "member", uniqueConstraints = [UniqueConstraint(name = "uk_member_email", columnNames = ["email"])]
)
class Member(
    email: String?, password: Password, nickname: String?, memberType: MemberType?
) : BaseEntity() {
    @field:Column(name = "email", nullable = false, unique = true)
    var email: String = validateEmail(email)
        protected set

    @field:Embedded
    var password: Password = password
        protected set

    @field:Column(name = "nickname", nullable = false, length = 45)
    var nickname: String = validateNickname(nickname)
        protected set

    @field:Column(name = "account_type", nullable = false)
    @field:Enumerated(EnumType.STRING)
    var memberType: MemberType = validateMemberType(memberType)
        protected set

    private fun validateEmail(email: String?): String {
        require(!email.isNullOrBlank()) {
            throw ApplicationException(ErrorCode.BLANK_EMAIL)
        }

        require(email.matches(EMAIL_REGEX)) {
            throw ApplicationException(ErrorCode.INVALID_EMAIL_FORMAT)
        }

        return email
    }

    private fun validateNickname(nickname: String?): String {
        require(!nickname.isNullOrBlank()) {
            throw ApplicationException(ErrorCode.BLANK_NICKNAME)
        }

        require(nickname.matches(NICKNAME_REGEX)) {
            throw ApplicationException(ErrorCode.INVALID_NICKNAME_FORMAT)
        }

        return nickname
    }

    private fun validateMemberType(memberType: MemberType?): MemberType {
        require(memberType != null) {
            throw ApplicationException(ErrorCode.BLANK_MEMBER_TYPE)
        }

        return memberType
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        private val NICKNAME_REGEX = "^[A-Za-z0-9가-힣]{8,15}$".toRegex()
    }
}
