package com.interrupt.server.auth.domain

typealias Token = AuthenticationCredentials

class AuthenticationCredentials(
    val jti: String,
    accessToken: String,
    refreshToken: String,
) {

    var accessToken: String = accessToken
        private set
    var refreshToken: String = refreshToken
        private set

    fun isSameRefreshToken(refreshToken: String): Boolean = (this.refreshToken == refreshToken)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthenticationCredentials

        return jti == other.jti
    }

    override fun hashCode(): Int {
        return jti.hashCode()
    }
}
