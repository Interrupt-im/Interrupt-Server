package com.interrupt.server.common.security

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException

fun addSalt(originalString: String, preSalt: String, postSalt: String): String = preSalt + originalString + postSalt

fun removeSalt(saltedString: String, preSalt: String, postSalt: String): String {
    if (saltedString.startsWith(preSalt).not() || saltedString.endsWith(postSalt).not()) throw InterruptServerException(errorCode = ErrorCode.NOT_SALTED_STRING)

    val startIndex = preSalt.length
    val endIndex = saltedString.length - postSalt.length

    return saltedString.substring(startIndex, endIndex)
}
