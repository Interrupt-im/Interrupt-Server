package com.interrupt.server.auth.service

import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.crypto.SecretKey

interface TokenProvider {

    val secretKey: SecretKey

    fun extractUsername(token: String): String?

    fun extractJti(token: String): String?

    fun extractExpiration(token: String): Date?

    fun buildToken(userDetails: UserDetails, jti: String, expiration: Long, additionalClaims: Map<String, Any> = emptyMap()): String

}