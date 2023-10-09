package com.interrupt.server.common.exception

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class InterruptServerException @JvmOverloads constructor(
    message: String? = null,
    override val cause: Throwable? = null,
    val errorCode: ErrorCode,
): RuntimeException(message, cause) {

    private val log: KLogger = KotlinLogging.logger {  }

    init {
        log.error {
            """
                server error
                cause: $cause
                message: $message
                errorCode: $errorCode
            """.trimIndent()
        }
    }

    override val message: String? = message
        get() = if (field.isNullOrBlank()) errorCode.message else "${errorCode.message}, $field"

}