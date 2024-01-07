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
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val jwtProperties: JwtProperties,
    private val objectMapper: ObjectMapper
) {

    private val secretKey = Keys.hmacShaKeyFor(
        jwtProperties.key.toByteArray()
    )

    fun extractUsername(token: String): String = extractAllClaims(token)?.subject.isValidClaimFromToken()

    fun extractJti(token: String): String = extractAllClaims(token)?.id.isValidClaimFromToken()

    private fun String?.isValidClaimFromToken(): String =
        if (isNullOrBlank()) throw InterruptServerException(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED)
        else this

    private fun extractAllClaims(token: String): Claims? =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

    fun generateAccessToken(userDetails: UserDetails, jti: String): String =
        buildToken(userDetails, jti, jwtProperties.accessTokenExpiration)

    fun generateRefreshToken(userDetails: UserDetails, jti: String): String =
        buildToken(userDetails, jti, jwtProperties.refreshTokenExpiration)

    fun generateAuthenticationCredentials(user: Member): AuthenticationCredentials {
        val jti = UUID.randomUUID().toString()

        val accessToken = generateAccessToken(user, jti)
        val refreshToken = generateRefreshToken(user, jti)

        val credentials = Credentials(accessToken, refreshToken)
        val identifier = Identifier(jti, user.id)

        return AuthenticationCredentials(credentials, identifier)
    }

    fun buildToken(userDetails: UserDetails, jti: String, expiration: Long, additionalClaims: Map<String, Any> = emptyMap()): String =
        Jwts.builder()
            .claims()
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .add(additionalClaims)
            .id(jti)
            .and()
            .signWith(secretKey)
            .compact()

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean =
        (extractUsername(token) == userDetails.username) && isTokenActive(token)

    private fun isTokenActive(token: String): Boolean = extractExpiration(token)?.after(Date()) ?: false

    private fun extractExpiration(token: String): Date? = extractAllClaims(token)?.expiration

    fun checkTokenExpiredByTokenString(token: String): Boolean {
        val parts = token.split(".")

        if (parts.size != 3) throw InterruptServerException(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED)

        val payload = String(Base64.getDecoder().decode(parts[1]))

        val expiration =
            objectMapper.readValue(payload, object : TypeReference<MutableMap<String, String>>() {})["exp"]?.toLong()
            ?: throw InterruptServerException(ErrorCode.SUSPICIOUS_ACTIVITY_DETECTED)

        val current = System.currentTimeMillis() / 1000

        return expiration < current
    }

}