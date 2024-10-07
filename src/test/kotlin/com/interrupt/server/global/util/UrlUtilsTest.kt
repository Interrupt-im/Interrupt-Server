package com.interrupt.server.global.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class UrlUtilsTest : BehaviorSpec({

    Given("와일드 카드 패턴의 url 과") {
        val pattern = "/uri/wild-card/**"

        When(" 해당 url 하위 계층 url 을 비교 하면") {
            val url = "/uri/wild-card/foo"

            val actual = matchUrl(pattern, url)

            Then("true 를 반환 한다") {
                actual shouldBe true
            }
        }

        When(" 해당 url 과 같은 계층 url 을 비교 하면") {
            val url = "/uri/wild-card"

            val actual = matchUrl(pattern, url)

            Then("true 를 반환 한다") {
                actual shouldBe true
            }
        }
    }
})
