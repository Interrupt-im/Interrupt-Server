package com.interrupt.server.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("rsa")
data class RsaKeyProperties(
    val publicKey: String,
    val privateKey: String
)