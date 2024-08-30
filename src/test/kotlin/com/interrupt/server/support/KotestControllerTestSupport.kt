package com.interrupt.server.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.interrupt.server.global.exception.ErrorCode
import com.interrupt.server.member.application.MemberCommandService
import com.interrupt.server.member.application.MemberQueryRepository
import com.interrupt.server.member.application.MemberQueryService
import com.interrupt.server.member.presentation.MemberApi
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.clearAllMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(
    controllers = [
        MemberApi::class,
    ]
)
@MockkBean(JpaMetamodelMappingContext::class)
abstract class KotestControllerTestSupport : BehaviorSpec() {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockkBean
    protected lateinit var memberCommandService: MemberCommandService

    @MockkBean
    protected lateinit var memberQueryService: MemberQueryService

    init {
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
