package com.interrupt.server.member.service

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.dto.register.MemberRegisterResponse
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val stringEncoder: StringEncoder,
) {

    @Value("\${security.encrypt-key}")
    private lateinit var secretKey: String

    @Value("\${security.id-pre-salt}")
    private lateinit var idPreSalt: String
    @Value("\${security.id-post-salt}")
    private lateinit var idPostSalt: String

    @Value("\${security.pw-pre-salt}")
    private lateinit var pwPreSalt: String
    @Value("\${security.pw-post-salt}")
    private lateinit var pwPostSalt: String

    @Value("\${security.name-pre-salt}")
    private lateinit var namePreSalt: String
    @Value("\${security.name-post-salt}")
    private lateinit var namePostSalt: String

    @Value("\${security.email-pre-salt}")
    private lateinit var emailPreSalt: String
    @Value("\${security.email-post-salt}")
    private lateinit var emailPostSalt: String

    @Transactional
    fun registerMember(memberRegisterRequest: MemberRegisterRequest): MemberRegisterResponse {
        val memberRegisterDto = memberRegisterRequest.copy(
            loginId = addSalt(memberRegisterRequest.loginId, idPreSalt, idPostSalt),
            password = addSalt(memberRegisterRequest.password, pwPreSalt, pwPostSalt),
            name = addSalt(memberRegisterRequest.name, namePreSalt, namePostSalt),
            email = addSalt(memberRegisterRequest.email, emailPreSalt, emailPostSalt),
        )
        
        val encryptedLoginId = stringEncoder.encrypt(memberRegisterDto.loginId, secretKey)

        val foundMember = memberRepository.findByLoginId(encryptedLoginId)
        validDuplicatedLoginId(foundMember)

        val member = memberRegisterDto.also {
            it.loginId = stringEncoder.encrypt(it.loginId, secretKey)
            it.password = stringEncoder.encrypt(it.password, secretKey)
            it.name = stringEncoder.encrypt(it.name, secretKey)
            it.email = stringEncoder.encrypt(it.email, secretKey)
        }.toEntity()

        val registeredMember = memberRepository.save(member)

        return MemberRegisterResponse.of(registeredMember).also {
            it.loginId = removeSalt(stringEncoder.decrypt(it.loginId, secretKey), idPreSalt, idPostSalt)
            it.name = removeSalt(stringEncoder.decrypt(it.name, secretKey), namePreSalt, namePostSalt)
            it.email = removeSalt(stringEncoder.decrypt(it.email, secretKey), emailPreSalt, emailPostSalt)
        }
    }

    private fun validDuplicatedLoginId(foundMember: Member?) {
        if (foundMember != null) {
            throw InterruptServerException(errorCode = ErrorCode.DUPLICATED_LOGIN_ID)
        }
    }

    private fun addSalt(originalString: String, preSalt: String, postSalt: String): String = preSalt + originalString + postSalt

    private fun removeSalt(saltedString: String, preSalt: String, postSalt: String): String {
        if (saltedString.startsWith(preSalt).not() || saltedString.endsWith(postSalt).not()) throw InterruptServerException(errorCode = ErrorCode.NOT_SALTED_STRING)

        val startIndex = preSalt.length
        val endIndex = saltedString.length - postSalt.length

        return saltedString.substring(startIndex, endIndex)
    }

}