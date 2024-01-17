package com.interrupt.server.auth.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.interrupt.server.auth.config.JwtProperties
import io.mockk.mockk

class JwtServiceTest {

    private val jwtProperties: JwtProperties = mockk()
    private val objectMapper: ObjectMapper = mockk()

    private val jwtService: JwtService = JwtService(jwtProperties, objectMapper)

}