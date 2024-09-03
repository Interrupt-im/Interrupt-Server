package com.interrupt.server.auth.fake

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.interrupt.server.auth.application.JwtService

class FakeJwtService : JwtService(FakeTokenProvider(), FakeJwtProperties.`만료 시간 5초`.properties, jacksonObjectMapper())
