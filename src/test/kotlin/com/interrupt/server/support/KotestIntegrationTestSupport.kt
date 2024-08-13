package com.interrupt.server.support

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
abstract class KotestIntegrationTestSupport : BehaviorSpec() {

    @Autowired
    private lateinit var cleanUp: DbCleanUp

    init {
        afterAny {
            cleanUp.all()
        }
    }

    override fun extensions(): List<Extension> = listOf(SpringExtension)
}
