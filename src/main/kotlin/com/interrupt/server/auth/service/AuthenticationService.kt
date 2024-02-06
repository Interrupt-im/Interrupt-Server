package com.interrupt.server.auth.service

import com.interrupt.server.auth.config.JwtProperties
import com.interrupt.server.auth.dto.login.SignInRequest
import com.interrupt.server.auth.dto.login.SignInResponse
import com.interrupt.server.auth.dto.refresh.TokenRefreshRequest
import com.interrupt.server.auth.dto.refresh.TokenRefreshResponse
import com.interrupt.server.auth.entity.AuthenticationCredentials
import com.interrupt.server.auth.entity.TokenCache
import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberQueryRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthenticationService(
    private val tokenRedisRepository: TokenRedisRepository,
    private val memberQueryRepository: MemberQueryRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val jwtProperties: JwtProperties,
) {

    fun login(request: SignInRequest): SignInResponse {
        val member: Member = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.loginId, request.password)
        ).principal as Member

        val authenticationCredentials = jwtService.generateAuthenticationCredentials(member)

        saveTokenCache(authenticationCredentials.identifier.public, authenticationCredentials)

        return authenticationCredentials.credentials.run { SignInResponse(accessToken, refreshToken) }
    }

    fun refreshToken(request: TokenRefreshRequest): TokenRefreshResponse {
        val presentedRefreshToken = request.refreshToken

        val loginId: String = jwtService.getUsername(presentedRefreshToken)
        val jti = jwtService.getJti(presentedRefreshToken)

        val user = memberQueryRepository.findByLoginId(loginId).isValidMember()

        val foundTokens = tokenRedisRepository.findById(jti).isValidToken()

        validateRefreshToken(presentedRefreshToken, foundTokens.credentials.refreshToken, user)

        validateActiveAccessToken(foundTokens.credentials.accessToken, jti)

        val authenticationCredentials = jwtService.generateAuthenticationCredentials(user)

        saveTokenCache(authenticationCredentials.identifier.public, authenticationCredentials)
        tokenRedisRepository.deleteById(jti)

        return authenticationCredentials.credentials.run { TokenRefreshResponse(accessToken, refreshToken) }
    }

    private fun validateActiveAccessToken(accessToken: String, jti: String) {
        if (!jwtService.checkTokenExpiredByTokenString(accessToken)) {
            tokenRedisRepository.deleteById(jti)
            throw InterruptServerException(ErrorCode.INVALID_TOKEN_REISSUE_REQUEST)
        }
    }

    private fun saveTokenCache(uuid: String, authenticationCredentials: AuthenticationCredentials) {
        val tokenCache = TokenCache(uuid, authenticationCredentials, jwtProperties.refreshTokenExpiration)

        tokenRedisRepository.save(tokenCache)
    }

    private fun validateRefreshToken(jwt: String, refreshToken: String, user: UserDetails) {
        if (jwt != refreshToken) throw InterruptServerException(ErrorCode.MISS_MATCH_TOKEN)
        if (!jwtService.isTokenValid(jwt, user)) throw InterruptServerException(ErrorCode.INVALID_REFRESH_TOKEN)
    }

    private fun Member?.isValidMember(): Member = this ?: throw InterruptServerException(ErrorCode.MEMBER_NOT_FOUND)

    private fun AuthenticationCredentials?.isValidToken(): AuthenticationCredentials =
        this?.let { this } ?: throw throw InterruptServerException(ErrorCode.TOKEN_NOT_FOUND)

}



