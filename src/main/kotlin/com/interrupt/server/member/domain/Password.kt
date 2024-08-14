package com.interrupt.server.member.domain

import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.springframework.security.crypto.password.PasswordEncoder

@Embeddable
class Password(
    password: String?,
    passwordEncoder: PasswordEncoder,
) {
    @field:Column(name = "password", nullable = false, length = 100)
    var password: String = validatePassword(password, passwordEncoder)
        protected set

    private fun validatePassword(password: String?, passwordEncoder: PasswordEncoder): String {
        require(!password.isNullOrBlank()) {
            throw ApplicationException(ErrorCode.BLANK_PASSWORD)
        }

        require(password.matches(PASSWORD_REGEX)) {
            throw ApplicationException(ErrorCode.INVALID_PASSWORD_FORMAT)
        }

        return passwordEncoder.encode(password)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Password) return false

        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        return password.hashCode()
    }


    companion object {
        private val PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9!@#$%^&*()_+.,;:<>?-]{8,15}$".toRegex()
    }
}
