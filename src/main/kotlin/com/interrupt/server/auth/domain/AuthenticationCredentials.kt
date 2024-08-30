package com.interrupt.server.auth.domain

class AuthenticationCredentials(
    val jti: String,
    val accessToken: String,
    val refreshToken: String,
) {
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
