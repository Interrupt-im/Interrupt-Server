package com.interrupt.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InterruptServerApplication

fun main(args: Array<String>) {
    runApplication<InterruptServerApplication>(*args)
}
