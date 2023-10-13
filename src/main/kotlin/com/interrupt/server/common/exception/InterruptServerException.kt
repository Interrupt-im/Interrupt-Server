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
                message: ${this.message}
                errorCode: $errorCode
            """.trimIndent()
        }
    }

    override val message: String = message ?: errorCode.message

}