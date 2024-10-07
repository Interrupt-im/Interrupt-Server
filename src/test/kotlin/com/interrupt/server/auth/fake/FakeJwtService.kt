package com.interrupt.server.auth.fake

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.interrupt.server.auth.application.JwtService
import java.time.ZonedDateTime

class FakeJwtService(private val tokenProvider: FakeTokenProvider = FakeTokenProvider()) : JwtService(tokenProvider, FakeJwtProperties.`만료 시간 5초`.properties, jacksonObjectMapper()){

    override fun getUsername(token: String): String = tokenProvider.extractUsername(token)!!

    override fun getJti(token: String): String = tokenProvider.extractJti(token) ?: ""

    override fun checkTokenExpiredByTokenString(token: String): Boolean {
        val expiration = tokenProvider.extractExpiration(token)

        return expiration?.let { ZonedDateTime.now().toInstant().isAfter(it.toInstant()) } ?: true
    }
}
