package com.interrupt.server.auth.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.interrupt.server.auth.config.JwtProperties
import com.interrupt.server.auth.entity.AuthenticationCredentials
import com.interrupt.server.auth.entity.Credentials
import com.interrupt.server.auth.entity.Identifier
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.member.entity.Member
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*

@Service
class JwtService(
    private val tokenProvider: TokenProvider,
    private val jwtProperties: JwtProperties,
    private val objectMapper: ObjectMapper
) {

    fun getUsername(token: String): String = tokenProvider.extractUsername(token).isValidClaimFromToken()

    fun getJti(token: String): String = tokenProvider.extractJti(token).isValidClaimFromToken()

    private fun String?.isValidClaimFromToken(): String =
        if (isNullOrBlank()) throw InterruptServerException(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED)
        else this

    fun generateAccessToken(userDetails: UserDetails, jti: String): String =
        tokenProvider.buildToken(userDetails, jti, jwtProperties.accessTokenExpiration)

    fun generateRefreshToken(userDetails: UserDetails, jti: String): String =
        tokenProvider.buildToken(userDetails, jti, jwtProperties.refreshTokenExpiration)

    fun generateAuthenticationCredentials(user: Member): AuthenticationCredentials {
        val jti = UUID.randomUUID().toString()

        val accessToken = generateAccessToken(user, jti)
        val refreshToken = generateRefreshToken(user, jti)

        val credentials = Credentials(accessToken, refreshToken)
        val identifier = Identifier(jti, user.id)

        return AuthenticationCredentials(credentials, identifier)
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean =
        (getUsername(token) == userDetails.username) && isTokenActive(token)

    private fun isTokenActive(token: String): Boolean = tokenProvider.extractExpiration(token)?.after(Date()) ?: false

    fun checkTokenExpiredByTokenString(token: String): Boolean {
        val parts = token.split(".")

        if (parts.size != 3) throw InterruptServerException(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED)

        val payload = String(Base64.getDecoder().decode(parts[1]))

        val expiration =
            objectMapper.readValue(payload, object : TypeReference<MutableMap<String, String>>() {})["exp"]?.toLong()
            ?: throw InterruptServerException(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED)

        val current = ZonedDateTime.now().toInstant().toEpochMilli() / 1000

        return expiration < current
    }

}