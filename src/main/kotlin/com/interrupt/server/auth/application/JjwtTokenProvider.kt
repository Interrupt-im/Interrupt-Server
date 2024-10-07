package com.interrupt.server.auth.application

import com.interrupt.server.member.domain.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JjwtTokenProvider(
    jwtProperties: JwtProperties,
) : TokenProvider {
    override val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    override fun extractUsername(token: String): String? = extractAllClaims(token)?.subject

    override fun extractJti(token: String): String? = extractAllClaims(token)?.id

    override fun extractExpiration(token: String): Date? = extractAllClaims(token)?.expiration

    private fun extractAllClaims(token: String): Claims? =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

    override fun buildToken(
        member: Member,
        jti: String,
        expiration: Long,
        additionalClaims: Map<String, Any>,
    ): String =
        Jwts.builder()
            .claims()
            .subject(member.email)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .add(additionalClaims)
            .id(jti)
            .and()
            .signWith(secretKey)
            .compact()
}
