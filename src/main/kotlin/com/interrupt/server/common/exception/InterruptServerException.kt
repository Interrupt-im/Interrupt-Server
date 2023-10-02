package com.interrupt.server.common.exception

class InterruptServerException @JvmOverloads constructor(
    message: String? = null,
    override val cause: Throwable? = null,
    val errorCode: ErrorCode,
): RuntimeException(message, cause) {

    override val message: String? = message
        get() = if (field.isNullOrBlank()) errorCode.message else "${errorCode.message}, $field"

}