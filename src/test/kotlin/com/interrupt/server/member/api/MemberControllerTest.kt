package com.interrupt.server.member.api

import com.interrupt.server.ControllerTestSupport
import com.interrupt.server.member.dto.delete.MemberDeleteRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckRequest
import com.interrupt.server.member.dto.duplicatedidcheck.LoginIdDuplicateCheckResponse
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.dto.update.MemberUpdateRequest
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MemberControllerTest: ControllerTestSupport() {

    @Test
    fun `회원 가입을 하기 전 회원 Id 중복 확인을 한다`() {
        // given
        val request = LoginIdDuplicateCheckRequest("loginId1")

        every { memberService.checkLoginIdDuplication(any<LoginIdDuplicateCheckRequest>()) } returns LoginIdDuplicateCheckResponse(true)

        // when then
        mockMvc.perform(
            get("/api/v1/members/check-login-id")
                .param("loginId", request.loginId))
            .andDo(print())
            .isOkBaseResponse()
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty)
    }

    @Test
    fun `아이디 중복 확인 시 회원 Id 는 필수 값 이다`() {
        // given
        val request = LoginIdDuplicateCheckRequest("")

        // when then
        mockMvc.perform(
            get("/api/v1/members/check-login-id")
                .param("loginId", request.loginId))
            .andDo(print())
            .isInvalidInputValueResponse("loginId")
    }

    @Test
    fun `아이디 중복 확인 시 회원 Id 는 8~20자로 이루어져아 한다`() {
        // given
        val request = LoginIdDuplicateCheckRequest("loginId")

        // when then
        mockMvc.perform(
            get("/api/v1/members/check-login-id")
                .param("loginId", request.loginId))
            .andDo(print())
            .isInvalidInputValueResponse("loginId")
    }

    @Test
    fun `아이디 중복 확인 시 회원 Id 는 영문 또는 숫자로 이루어져야 한다`() {
        // given
        val request = LoginIdDuplicateCheckRequest("회원Id")

        // when then
        mockMvc.perform(
            get("/api/v1/members/check-login-id")
                .param("loginId", request.loginId))
            .andDo(print())
            .isInvalidInputValueResponse("loginId")
    }

    @Test
    fun `회원 가입`() {
        // given
        val request = MemberRegisterRequest("memberId", "word123!", "홍길동", "email@domain.com", "0000")

        justRun { memberService.registerMember(request) }

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isCreatedBaseResponse()
    }

    @Test
    fun `회원 가입 시 회원 Id 는 필수 값 이다`() {
        // given
        val request = MemberRegisterRequest("", "word123!", "홍길동", "email@domain.com", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("loginId")
    }

    @Test
    fun `회원 가입 시 회원 Id 는 8~20자로 이루어져아 한다`() {
        // given
        val request = MemberRegisterRequest("loginId", "password123!", "홍길동", "email@domain.com", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("loginId")
    }

    @Test
    fun `회원 가입 시 회원 Id 는 영문 또는 숫자로 이루어져야 한다`() {
        // given
        val request = MemberRegisterRequest("회원Id", "password123!", "홍길동", "email@domain.com", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("loginId")
    }

    @Test
    fun `회원 가입 시 비밀번호는 필수 값 이다`() {
        // given
        val request = MemberRegisterRequest("memberId", "", "홍길동", "email@domain.com", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password")
    }

    @Test
    fun `회원 가입 시 비밀번호 는 8~20자로 이루어져아 한다`() {
        // given
        val request = MemberRegisterRequest("loginId", "word123", "홍길동", "email@domain.com", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password")
    }

    @Test
    fun `회원 가입 시 비밀번호 는 영문과 숫자가 모두 포함되어야 한다`() {
        // given
        val request = MemberRegisterRequest("회원Id", "password!", "홍길동", "email@domain.com", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password")
    }

    @Test
    fun `회원 가입 시 이름은 필수 값 이다`() {
        // given
        val request = MemberRegisterRequest("memberId", "word123!", "", "email@domain.com", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("name")
    }

    @Test
    fun `회원 가입 시 이메일은 필수 값 이다`() {
        // given
        val request = MemberRegisterRequest("memberId", "word123!", "홍길동", "", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("email")
    }

    @Test
    fun `회원 가입 시 이메일은 올바른 형식이어야 한다`() {
        // given
        val request = MemberRegisterRequest("memberId", "word123!", "홍길동", "emaildomain", "0000")

        // when then
        mockMvc.perform(
            post("/api/v1/members")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("email")
    }

    @Test
    fun `회원 수정`() {
        // given
        val request = MemberUpdateRequest("newPassword123!", "홍길동", "email@domail.com", "0000")

        justRun { memberService.updateMember( any<String>(), request) }

        // when then
        mockMvc.perform(
            patch("/api/v1/members/{loginId}", "memberId")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent)
    }

    @Test
    fun `회원 수정 시 비밀번호가 존재한다면 8~20자로 이루어져아 한다`() {
        // given
        val request = MemberUpdateRequest(password = "new1234")

        // when then
        mockMvc.perform(
            patch("/api/v1/members/{loginId}", "memberId")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password")
    }

    @Test
    fun `회원 수정 시 비밀번호가 존재한다면 영문과 숫자는 모두 포함되어야 한다`() {
        // given
        val request = MemberUpdateRequest(password = "newPassword!")

        // when then
        mockMvc.perform(
            patch("/api/v1/members/{loginId}", "memberId")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password")
    }

    @Test
    fun `회원 수정 시 이메일이 존재한다면 올바른 형식이어야 한다`() {
        // given
        val request = MemberUpdateRequest(email = "emaildomain")

        // when then
        mockMvc.perform(
            patch("/api/v1/members/{loginId}", "memberId")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("email")
    }
    
    @Test
    fun `회원 탈퇴'`() {
        // given
        val request = MemberDeleteRequest("ward123!")

        justRun { memberService.deleteMember(any<String>(), request) }

        // when then
        mockMvc.perform(
            delete("/api/v1/members/{loginId}", "memberId")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent)
    }

    @Test
    fun `회원 탈퇴 시 비밀번호는 필수 값 이다`() {
        // given
        val request = MemberDeleteRequest("")

        // when then
        mockMvc.perform(
            delete("/api/v1/members/{loginId}", "memberId")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password")
    }

    @Test
    fun `회원 탈퇴 시 비밀번호는 8~20자로 이루어져아 한다`() {
        // given
        val request = MemberDeleteRequest("new1234")

        // when then
        mockMvc.perform(
            delete("/api/v1/members/{loginId}", "memberId")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password")
    }

    @Test
    fun `회원 탈퇴 시 비밀번호 는 영문과 숫자가 모두 포함되어야 한다`() {
        // given
        val request = MemberDeleteRequest("newPassword!")

        // when then
        mockMvc.perform(
            delete("/api/v1/members/{loginId}", "memberId")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .isInvalidInputValueResponse("password")
    }

}