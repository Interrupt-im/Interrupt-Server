package com.interrupt.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.interrupt.server.auth.api.AuthController
import com.interrupt.server.auth.config.SecurityConfiguration
import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.auth.service.AuthenticationService
import com.interrupt.server.auth.service.JwtService
import com.interrupt.server.auth.web.JwtAuthenticationFilter
import com.interrupt.server.common.config.ServerConfig
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.member.api.MemberController
import com.interrupt.server.member.service.MemberService
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebMvcTest(
    controllers = [
        MemberController::class,
        AuthController::class
    ],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [
                ServerConfig::class,
            ]
        )
    ],
    includeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [
                JwtAuthenticationFilter::class,
                SecurityConfiguration::class,
            ]
        )
    ]
)
@MockkBean(JpaMetamodelMappingContext::class)
abstract class ControllerTestSupport {

    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockkBean(relaxed = true)
    private lateinit var h2ConsoleProperties: H2ConsoleProperties
    @MockkBean
    private lateinit var jwtService: JwtService
    @MockkBean
    private lateinit var userDetailsService: UserDetailsService
    @MockkBean
    private lateinit var tokenRedisRepository: TokenRedisRepository
    @MockkBean
    private lateinit var authenticationProvider: AuthenticationProvider
    @MockkBean
    private lateinit var logoutHandler: LogoutHandler
    @MockkBean
    private lateinit var entryPoint: AuthenticationEntryPoint
    @MockkBean
    private lateinit var accessDeniedHandler: AccessDeniedHandler

    @MockkBean
    protected lateinit var memberService: MemberService

    @MockkBean
    protected lateinit var authService: AuthenticationService

    @BeforeEach
    fun setUp(context: WebApplicationContext) {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .defaultRequest<DefaultMockMvcBuilder>(post("/**").with(csrf()))
            .defaultRequest<DefaultMockMvcBuilder>(patch("/**").with(csrf()))
            .defaultRequest<DefaultMockMvcBuilder>(delete("/**").with(csrf()))
            .build()
    }

    protected fun ResultActions.baseResponse(status: HttpStatus): ResultActions =
        this.andExpect(jsonPath("$.statusCode").value(status.value()))
            .andExpect(jsonPath("$.message").value("success"))


    protected fun ResultActions.isOkBaseResponse(): ResultActions =
        this.andExpect(status().isOk)
            .baseResponse(HttpStatus.OK)


    protected fun ResultActions.isCreatedBaseResponse(): ResultActions =
        this.andExpect(status().isCreated)
            .baseResponse(HttpStatus.CREATED)

    protected fun ResultActions.isInvalidInputValueResponse(invalidParam: String, message: String): ResultActions =
        this.andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.statusCode").value(ErrorCode.INVALID_INPUT_VALUE.status.value()))
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_INPUT_VALUE.name))
            .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.message))
            .andExpect(jsonPath("$.data").isNotEmpty)
            .andExpect(jsonPath("$.data.$invalidParam").value(message))

}