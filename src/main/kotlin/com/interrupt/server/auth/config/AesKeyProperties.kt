package com.interrupt.server.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("aes")
data class AesKeyProperties(
    val secretKey: String,
    val iv: String,
    val algorithm: String,
    val transformation: String,
)