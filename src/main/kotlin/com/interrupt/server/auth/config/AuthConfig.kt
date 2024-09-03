package com.interrupt.server.auth.config

import com.interrupt.server.auth.application.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class AuthConfig
