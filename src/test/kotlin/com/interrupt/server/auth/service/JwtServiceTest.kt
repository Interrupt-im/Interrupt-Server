package com.interrupt.server.auth.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.interrupt.server.auth.config.JwtProperties
import io.mockk.mockk
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class JwtServiceTest {

    class MockTokenProvider: TokenProvider {

        private val keyBytes = ByteArray(256 / 8) { 0x01.toByte() }
        override val secretKey: SecretKey = SecretKeySpec(keyBytes, "HmacSHA256")

        override fun extractUsername(token: String): String? {
            TODO("Not yet implemented")
        }

        override fun extractJti(token: String): String? {
            TODO("Not yet implemented")
        }

        override fun extractExpiration(token: String): Date? {
            TODO("Not yet implemented")
        }

        override fun buildToken(userDetails: UserDetails, jti: String, expiration: Long, additionalClaims: Map<String, Any>): String {
            TODO("Not yet implemented")
        }

    }

    private val tokenProvider: TokenProvider = MockTokenProvider()
    private val jwtProperties: JwtProperties = mockk(relaxed = true)
    private val objectMapper: ObjectMapper = mockk()

    private val jwtService: JwtService = JwtService(tokenProvider, jwtProperties, objectMapper)


}