package com.interrupt.server.member.domain

import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.member.fixture.MemberFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.datatest.withData
import io.kotest.matchers.throwable.shouldHaveMessage

class MemberTest : BehaviorSpec({
    Given("유효한 이메일, 비밀번호, 닉네임, 회원 유형인 경우") {
        val memberFixture = MemberFixture.`고객 1`

        Then("객체를 정상적으로 생성할 수 있다") {
            shouldNotThrowAny {
                Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
            }
        }
    }

    Given("잘못된 이메일로 회원 객체를 생성할 때") {
        When("이메일이 null 이라면") {
            val memberFixture = MemberFixture.`이메일 NULL 회원`

            Then("예외를 던진다") {
                shouldThrowExactly<ApplicationException> {
                    Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                } shouldHaveMessage "이메일 값이 없습니다."
            }
        }

        When("이메일이 공백 이라면") {
            val memberFixture = MemberFixture.`이메일 공백 회원`

            Then("예외를 던진다") {
                shouldThrowExactly<ApplicationException> {
                    Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                } shouldHaveMessage "이메일 값이 없습니다."
            }
        }

        withData(
            nameFn = { "잘못 된 이메일 형식 - $it(${it.email})" },
            listOf(
                MemberFixture.`이메일 형식 비정상 회원1`,
                MemberFixture.`이메일 형식 비정상 회원2`,
                MemberFixture.`이메일 형식 비정상 회원3`,
                MemberFixture.`이메일 형식 비정상 회원4`,
                MemberFixture.`이메일 형식 비정상 회원5`,
                MemberFixture.`이메일 형식 비정상 회원6`,
            )
        ) { memberFixture ->
            When("이메일 형식이 올바르지 않다면") {
                Then("예외를 던진다") {
                    shouldThrowExactly<ApplicationException> {
                        Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                    } shouldHaveMessage "이메일 형식이 올바르지 않습니다."
                }
            }
        }
    }

    Given("잘못된 비밀번호로 회원 객체를 생성할 때") {
        When("비밀번호가 null 이라면") {
            val memberFixture = MemberFixture.`비밀번호 NULL 회원`

            Then("예외를 던진다") {
                shouldThrowExactly<ApplicationException> {
                    Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                } shouldHaveMessage "비밀번호 값이 없습니다."
            }
        }

        When("비밀번호가 공백 이라면") {
            val memberFixture = MemberFixture.`비밀번호 공백 회원`

            Then("예외를 던진다") {
                shouldThrowExactly<ApplicationException> {
                    Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                } shouldHaveMessage "비밀번호 값이 없습니다."
            }
        }

        withData(
            nameFn = { "잘못 된 비밀번호 형식 - $it(${it.loginPassword})" },
            listOf(
                MemberFixture.`비밀번호 형식 비정상 회원1`,
                MemberFixture.`비밀번호 형식 비정상 회원2`,
                MemberFixture.`비밀번호 형식 비정상 회원3`,
                MemberFixture.`비밀번호 형식 비정상 회원4`,
                MemberFixture.`비밀번호 형식 비정상 회원5`,
            )
        ) { memberFixture ->
            When("비밀번호 형식이 올바르지 않다면") {
                Then("예외를 던진다") {
                    shouldThrowExactly<ApplicationException> {
                        Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                    } shouldHaveMessage "비밀번호는 영어, 숫자, 특수문자로 이루어진 8자 이상 15자 이하여야 합니다."
                }
            }
        }
    }

    Given("잘못된 닉네임으로 회원 객체를 생성할 때") {
        When("닉네임이 null 이라면") {
            val memberFixture = MemberFixture.`닉네임 NULL 회원`

            Then("예외를 던진다") {
                shouldThrowExactly<ApplicationException> {
                    Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                } shouldHaveMessage "닉네임 값이 없습니다."
            }
        }

        When("닉네임이 공백 이라면") {
            val memberFixture = MemberFixture.`닉네임 공백 회원`

            Then("예외를 던진다") {
                shouldThrowExactly<ApplicationException> {
                    Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                } shouldHaveMessage "닉네임 값이 없습니다."
            }
        }

        withData(
            nameFn = { "잘못 된 닉네임 형식 - $it(${it.nickname})" },
            listOf(
                MemberFixture.`닉네임 형식 비정상 회원1`,
                MemberFixture.`닉네임 형식 비정상 회원2`,
                MemberFixture.`닉네임 형식 비정상 회원3`,
            )
        ) { memberFixture ->
            When("닉네임 형식이 올바르지 않다면") {
                Then("예외를 던진다") {
                    shouldThrowExactly<ApplicationException> {
                        Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                    } shouldHaveMessage "닉네임은 한글, 영어, 숫자로 이루어진 8자 이상 15자 이하여야 합니다."
                }
            }
        }
    }

    Given("잘못된 회원 유형으로 회원 객체를 생성할 때") {
        When("회원 유형이 null 이라면") {
            val memberFixture = MemberFixture.`회원 유형 NULL 회원`

            Then("예외를 던진다") {
                shouldThrowExactly<ApplicationException> {
                    Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                } shouldHaveMessage "회원 유형 값이 없습니다."
            }
        }

        When("회원 유형이 공백 이라면") {
            val memberFixture = MemberFixture.`회원 유형 공백 회원`

            Then("예외를 던진다") {
                shouldThrowExactly<ApplicationException> {
                    Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                } shouldHaveMessage "회원 유형 값이 없습니다."
            }
        }

        When("존재 하지 않는 회원 유형 이라면") {
            val memberFixture = MemberFixture.`없는 회원 유형 회원`

            Then("예외를 던진다") {
                shouldThrowExactly<ApplicationException> {
                    Member(memberFixture.email, memberFixture.loginPassword, memberFixture.nickname, memberFixture.memberType)
                } shouldHaveMessage "올바른 회원 유형이 아닙니다."
            }
        }
    }
})
