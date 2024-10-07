package com.interrupt.server.support

import io.kotest.core.test.TestCase

fun TestCase.isWhen() = name.prefix?.contains("When", ignoreCase = true) ?: false
