package com.interrupt.server.auth.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.interrupt.server.auth.fake.FakeJwtProperties
import com.interrupt.server.auth.fake.FakeTokenProvider
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.member.fake.FakePasswordEncoder
import com.interrupt.server.member.fixture.MemberFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.ZonedDateTime
import java.util.*

@DisplayName("JwtService 테스트")
class JwtServiceTest : BehaviorSpec({
    val tokenProvider: TokenProvider = FakeTokenProvider()
    val jwtProperties: JwtProperties = FakeJwtProperties.`만료 시간 5초`.properties
    val objectMapper: ObjectMapper = jacksonObjectMapper()

    val jwtService = JwtService(tokenProvider, jwtProperties, objectMapper)

    fun currentNano(): Long = ZonedDateTime.now().toInstant().toEpochMilli() / 1000

    Given("토큰을 받아 username 을 추출 하여 반환할 때") {
        When("username 을 정상적으로 추출 했다면") {
            val tokenObject = FakeTokenProvider.FakeTokenObject("username", null, null)
            val tokenWithUsername = objectMapper.writeValueAsString(tokenObject)

            val actual = jwtService.getUsername(tokenWithUsername)

            Then("username 을 반환 한다") {
                actual.shouldNotBeBlank()
            }
        }

        When("username 이 null 이면") {
            val tokenObject = FakeTokenProvider.FakeTokenObject(null, null, null)
            val tokenWithoutUsername = objectMapper.writeValueAsString(tokenObject)

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    jwtService.getUsername(tokenWithoutUsername)
                } shouldHaveMessage "CLAIM 정보가 비어있습니다."
            }
        }

        When("username 이 공백 이면") {
            val tokenObject = FakeTokenProvider.FakeTokenObject("", null, null)
            val tokenWithBlankUsername = objectMapper.writeValueAsString(tokenObject)

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    jwtService.getUsername(tokenWithBlankUsername)
                } shouldHaveMessage "CLAIM 정보가 비어있습니다."
            }
        }
    }

    Given("토큰을 받아 jti 를 추출 하여 반환할 때") {
        When("jti 를 정상적으로 추출 했다면") {
            val tokenObject = FakeTokenProvider.FakeTokenObject(null, "jti", null)
            val tokenWithJti = objectMapper.writeValueAsString(tokenObject)

            val actual = jwtService.getJti(tokenWithJti)

            Then("jti 를 반환 한다") {
                actual shouldBe "jti"
            }
        }

        When("jti 가 null 이면") {
            val tokenObject = FakeTokenProvider.FakeTokenObject(null, null, null)
            val tokenWithoutJti = objectMapper.writeValueAsString(tokenObject)

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    jwtService.getJti(tokenWithoutJti)
                } shouldHaveMessage "CLAIM 정보가 비어있습니다."
            }
        }

        When("jti 가 공백 이면") {
            val tokenObject = FakeTokenProvider.FakeTokenObject(null, "", null)
            val tokenWithBlankJti = objectMapper.writeValueAsString(tokenObject)

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    jwtService.getJti(tokenWithBlankJti)
                } shouldHaveMessage "CLAIM 정보가 비어있습니다."
            }
        }
    }

    Given("userDetails 와 jti 를 받아") {
        val member = MemberFixture.`고객 1`.`회원 엔티티 생성`(FakePasswordEncoder.INSTANCE)
        val jti = "jti"

        When("accessToken 을 생성 후") {
            val actual = jwtService.generateAccessToken(member, jti)

            Then("accessToken 을 반환 한다") {
                actual.shouldNotBeBlank()
            }
        }

        When("refreshToken 을 생성 후") {
            val actual = jwtService.generateRefreshToken(member, jti)

            Then("refreshToken 을 반환 한다") {
                actual.shouldNotBeBlank()
            }
        }
    }

    Given("userDetails 를 받아") {
        val member = MemberFixture.`고객 1`.`회원 엔티티 생성`(FakePasswordEncoder.INSTANCE)

        When("토큰 쌍을 생성 후") {
            val actual = jwtService.generateAuthenticationCredentials(member)

            Then("반환 한다") {
                actual.shouldNotBeNull()
            }
        }
    }

    Given("토큰이 유효 한지 검증할 때") {
        val member = MemberFixture.`고객 1`.`회원 엔티티 생성`(FakePasswordEncoder.INSTANCE)
        val fakeTokenObject = FakeTokenProvider.FakeTokenObject(member.email, "jti", currentNano() + 5000L)
        val token = objectMapper.writeValueAsString(fakeTokenObject)

        When("정상적인 토큰 이라면") {
            val actual = jwtService.isTokenValid(token, member)

            Then("true 를 반환 한다") {
                actual.shouldBeTrue()
            }
        }

        When("claims 중 username 과 userDetails 의 username 이 다르다면") {
            val invalidMember = MemberFixture.`고객 2`.`회원 엔티티 생성`(FakePasswordEncoder.INSTANCE)

            val actual = jwtService.isTokenValid(token, invalidMember)

            Then("false 를 반환 한다") {
                actual.shouldBeFalse()
            }
        }

        When("claims 중 만료 시간이 지났다면") {
            val fakeExpiredTokenObject = FakeTokenProvider.FakeTokenObject(member.email, "jti", -5000L)
            val expiredToken = objectMapper.writeValueAsString(fakeExpiredTokenObject)

            val actual = jwtService.isTokenValid(expiredToken, member)

            Then("false 를 반환 한다") {
                actual.shouldBeFalse()
            }
        }

        When("claims 중 만료 시간이 null 이라면") {
            val fakeWithoutExpiredTokenObject = FakeTokenProvider.FakeTokenObject(member.email, "jti", null)
            val withoutExpiredToken = objectMapper.writeValueAsString(fakeWithoutExpiredTokenObject)

            val actual = jwtService.isTokenValid(withoutExpiredToken, member)

            Then("false 를 반환 한다") {
                actual.shouldBeFalse()
            }
        }
    }

    Given("토큰 문자열을 이용해 만료 시간을 체크할 때") {
        val member = MemberFixture.`고객 1`.`회원 엔티티 생성`(FakePasswordEncoder.INSTANCE)
        val encoder = Base64.getEncoder()

        When("만료 시간이 아직 지나지 않았다면") {
            val fakeTokenObject = FakeTokenProvider.FakeTokenObject(member.email, "jti", currentNano() + 5000L)
            val token = "header.${encoder.encode(objectMapper.writeValueAsString(fakeTokenObject).toByteArray()).toString(Charsets.UTF_8)}.sign"

            val actual = jwtService.checkTokenExpiredByTokenString(token)

            Then("false 를 반환 한다") {
                actual.shouldBeFalse()
            }
        }

        When("만료 시간이 지났다면") {
            val fakeTokenObject = FakeTokenProvider.FakeTokenObject(member.email, "jti", currentNano() - 5000L)
            val token = "header.${encoder.encode(objectMapper.writeValueAsString(fakeTokenObject).toByteArray()).toString(Charsets.UTF_8)}.sign"

            val actual = jwtService.checkTokenExpiredByTokenString(token)

            Then("true 를 반환 한다") {
                actual.shouldBeTrue()
            }
        }

        When("토큰에 만료 시간이 없다면") {
            val fakeTokenObject = FakeTokenProvider.FakeTokenObject(member.email, "jti", null)
            val token = "header.${encoder.encode(objectMapper.writeValueAsString(fakeTokenObject).toByteArray()).toString(Charsets.UTF_8)}.sign"

            Then("예외를 던진다") {
                shouldThrow<ApplicationException> {
                    jwtService.checkTokenExpiredByTokenString(token)
                } shouldHaveMessage "액세스 토큰이 유효하지 않습니다."
            }
        }
    }
})
