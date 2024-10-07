package com.interrupt.server.auth.application

import com.interrupt.server.auth.application.command.LogoutCommand
import com.interrupt.server.auth.application.command.TokenRefreshCommand
import com.interrupt.server.auth.domain.AuthenticationCredentials
import com.interrupt.server.member.application.MemberCommandRepository
import com.interrupt.server.member.fixture.MemberFixture
import com.interrupt.server.support.KotestIntegrationTestSupport
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@DisplayName("AuthCommandService 통합 테스트")
class AuthCommandServiceIntegrationTest : KotestIntegrationTestSupport() {

    @Autowired
    private lateinit var authCommandService: AuthCommandService

    @Autowired
    private lateinit var memberCommandRepository: MemberCommandRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Autowired
    private lateinit var jwtProperties: JwtProperties

    @Autowired
    private lateinit var tokenCommandRepository: TokenCommandRepository

    @Autowired
    private lateinit var tokenQueryRepository: TokenQueryRepository

    init {
        Given("이메일과 비밀번호를 받아") {
            val memberFixture = MemberFixture.`고객 1`
            val member = memberFixture.`회원 엔티티 생성`(passwordEncoder)
            memberCommandRepository.save(member)

            val command = memberFixture.`로그인 COMMAND 생성`()

            When("로그인을 하면") {
                val actual = authCommandService.login(command)

                Then("토큰을 반환 한다") {
                    actual shouldNotBe null
                }
            }
        }

        Given("토큰 키 값을 받아") {
            val member = MemberFixture.`고객 1`.`회원 엔티티 생성`(passwordEncoder)
            memberCommandRepository.save(member)
            val jti = UUID.randomUUID().toString()
            val accessToken = tokenProvider.buildToken(member, jti, 0L)
            val refreshToken = tokenProvider.buildToken(member, jti, jwtProperties.refreshTokenExpiration)
            val authenticationCredentials =
                tokenCommandRepository.save(AuthenticationCredentials(jti, accessToken, refreshToken))
            val logoutCommand = LogoutCommand(authenticationCredentials.jti)

            When("로그아웃을 하면") {
                authCommandService.logout(logoutCommand)

                Then("토큰 저장소에서 토큰이 삭제 된다") {
                    tokenQueryRepository.findByJti(jti) shouldBe null
                }
            }
        }

        Given("refreshToken 을 받아") {
            val member = MemberFixture.`고객 1`.`회원 엔티티 생성`(passwordEncoder)
            memberCommandRepository.save(member)
            val jti = UUID.randomUUID().toString()
            val accessToken = tokenProvider.buildToken(member, jti, 0L)
            val refreshToken = tokenProvider.buildToken(member, jti, jwtProperties.refreshTokenExpiration)
            val authenticationCredentials =
                tokenCommandRepository.save(AuthenticationCredentials(jti, accessToken, refreshToken))
            val tokenRefreshCommand = TokenRefreshCommand(member.email, authenticationCredentials.refreshToken)

            When("accessToken 이 만료되었다면") {
                val actual = authCommandService.refreshToken(tokenRefreshCommand)

                Then("새로운 토큰을 발급 한다") {
                    actual shouldNotBe null
                }
            }
        }
    }
}
