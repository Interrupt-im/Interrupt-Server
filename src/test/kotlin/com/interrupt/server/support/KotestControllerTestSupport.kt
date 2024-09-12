package com.interrupt.server.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.interrupt.server.auth.presentation.dto.response.LoginResponse
import com.interrupt.server.global.common.SuccessResponse
import com.interrupt.server.member.application.MemberCommandRepository
import com.interrupt.server.member.fixture.MemberFixture
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import io.kotest.extensions.spring.SpringExtension
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.mockk.clearAllMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
abstract class KotestControllerTestSupport : BehaviorSpec() {

    @LocalServerPort
    private var port: Int = 0

    protected lateinit var client: HttpClient

    private var _accessToken: String? = null
    private val accessToken: String
        get() = _accessToken ?: ""

    private var _refreshToken: String? = null
    protected val refreshToken: String
        get() = _refreshToken ?: ""

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var cleanUp: DbCleanUp

    @Autowired
    private lateinit var redisCleanUp: RedisCleanUp

    @Autowired
    private lateinit var memberCommandRepository: MemberCommandRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    init {
        beforeSpec {
            client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    jackson()
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }

                defaultRequest {
                    url("http://localhost")
                    port = this@KotestControllerTestSupport.port
                    contentType(ContentType.Application.Json)
                    bearerAuth(accessToken)
                }
            }
        }

        beforeContainer {
            if (it.isWhen()) {
                val member = MemberFixture.`고객 1`.`회원 엔티티 생성`(passwordEncoder)
                memberCommandRepository.save(member)
            }
        }

        afterContainer {
            if (it.a.isWhen()) {
                clearAllMocks()
                cleanUp.all()
                redisCleanUp.all()
                `토큰 값 초기화`()
            }
        }

        afterSpec {
            client.close()
        }
    }

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    protected suspend fun BehaviorSpecGivenContainerScope.WhenWithAuth(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) {
        When(name) {
            `로그인 셋업`()

            this.test()
        }
    }

    private suspend fun `로그인 셋업`() {
        val response: SuccessResponse<LoginResponse> = client.post("/api/auth/login") {
            setBody(MemberFixture.`고객 1`.`로그인 요청 DTO 생성`())
        }.body()

        _accessToken = response.data?.accessToken
        _refreshToken = response.data?.refreshToken
    }

    private suspend fun `토큰 값 초기화`() {
        this._accessToken = null
        this._refreshToken = null
    }
}
