package com.interrupt.server.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.interrupt.server.member.application.MemberCommandService
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearAllMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
abstract class KotestControllerTestSupport : BehaviorSpec() {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var cleanUp: DbCleanUp

    @Autowired
    private lateinit var redisCleanUp: RedisCleanUp

    init {
        beforeAny {

        }

        afterAny {
            cleanUp.all()
            redisCleanUp.all()
        }

        afterContainer {
            clearAllMocks()
        }
    }

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    protected fun ResultActions.isStatusAs(status: HttpStatus): ResultActions =
        this.andExpectAll(
            MockMvcResultMatchers.status().`is`(status.value()),
            MockMvcResultMatchers.jsonPath("$.meta.code").value(status.value()),
            MockMvcResultMatchers.jsonPath("$.meta.message").value(status.reasonPhrase),
        )
}
