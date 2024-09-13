package com.interrupt.server.auth.application

import com.interrupt.server.auth.application.command.LoginCommand
import com.interrupt.server.auth.application.command.LogoutCommand
import com.interrupt.server.auth.application.command.TokenRefreshCommand
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
    private val tokenQueryRepository: TokenQueryRepository,
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

    fun refreshToken(tokenRefreshCommand: TokenRefreshCommand): AuthenticationCredentials {
        val presentedRefreshToken = tokenRefreshCommand.refreshToken

        val member = memberQueryRepository.findByEmailAndNotDeleted(tokenRefreshCommand.email) ?: throw ApplicationException(ErrorCode.MEMBER_NOT_FOUND)

        val foundToken = tokenQueryRepository.findByJti(jwtService.getJti(presentedRefreshToken!!))
        validateToken(foundToken, presentedRefreshToken, member)

        val authenticationCredentials = jwtService.generateAuthenticationCredentials(member)

        tokenCommandRepository.save(authenticationCredentials)
        tokenCommandRepository.deleteByJti(foundToken!!.jti)

        return authenticationCredentials
    }

    private fun validateToken(
        authenticationCredentials: AuthenticationCredentials?,
        refreshToken: String,
        member: Member,
    ) {
        check (authenticationCredentials != null) {
            throw ApplicationException(ErrorCode.TOKEN_NOT_FOUND)
        }

        check (jwtService.isTokenValid(refreshToken, member)) {
            throw ApplicationException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        check(authenticationCredentials.isSameRefreshToken(refreshToken)) {
            throw ApplicationException(ErrorCode.MISS_MATCH_TOKEN)
        }

        validateActiveAccessToken(authenticationCredentials.accessToken, authenticationCredentials.jti)
    }

    private fun validateActiveAccessToken(
        accessToken: String,
        jti: String,
    ) {
        if (!jwtService.checkTokenExpiredByTokenString(accessToken)) {
            tokenCommandRepository.deleteByJti(jti)
            throw ApplicationException(ErrorCode.INVALID_TOKEN_REISSUE_REQUEST)
        }
    }
}
