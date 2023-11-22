package com.interrupt.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class InterruptServerApplication

fun main(args: Array<String>) {
    runApplication<InterruptServerApplication>(*args)
}
