package com.interrupt.server.common.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityConfig {

    @Bean
    fun stringEncoder(): StringEncoder = StringEncoder()

}