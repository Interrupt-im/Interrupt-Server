package com.interrupt.server.auth.application

import com.interrupt.server.auth.application.command.LoginCommand
import com.interrupt.server.auth.application.command.LogoutCommand
import com.interrupt.server.auth.domain.AuthenticationCredentials
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode
import com.interrupt.server.member.application.MemberQueryRepository
import com.interrupt.server.member.application.PasswordService
import com.interrupt.server.member.domain.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthCommandService(
    private val memberQueryRepository: MemberQueryRepository,
    private val passwordService: PasswordService,
    private val jwtService: JwtService,
    private val tokenCommandRepository: TokenCommandRepository,
) {

    fun login(loginCommand: LoginCommand): AuthenticationCredentials {
        val member = memberQueryRepository.findByEmailAndNotDeleted(loginCommand.email).validateLoginField(loginCommand)

        val authenticationCredentials = jwtService.generateAuthenticationCredentials(member)
        tokenCommandRepository.save(authenticationCredentials)

        return authenticationCredentials
    }

    private fun Member?.validateLoginField(loginCommand: LoginCommand): Member {
        check(this != null&& passwordService.matches(loginCommand.password, password)) {
            throw ApplicationException(ErrorCode.NOT_MATCH_LOGIN_FIELD)
        }

        return this
    }

    fun logout(logoutCommand: LogoutCommand) {
        tokenCommandRepository.deleteByJti(logoutCommand.jti)
    }
}
