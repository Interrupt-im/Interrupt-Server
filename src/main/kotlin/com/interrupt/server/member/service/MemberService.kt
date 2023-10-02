package com.interrupt.server.member.service

import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import com.interrupt.server.common.security.StringEncoder
import com.interrupt.server.member.dto.register.MemberRegisterRequest
import com.interrupt.server.member.dto.register.MemberRegisterResponse
import com.interrupt.server.member.entity.Member
import com.interrupt.server.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val stringEncoder: StringEncoder,
) {

    @Transactional
    fun registerMember(memberRegisterRequest: MemberRegisterRequest): MemberRegisterResponse {
        val encryptedLoginId = stringEncoder.encrypt(memberRegisterRequest.loginId)

        val foundMember = memberRepository.findByLoginId(encryptedLoginId)
        validDuplicatedLoginId(foundMember)

        val member = memberRegisterRequest.toEntity(stringEncoder)

        val registeredMember = memberRepository.save(member)

        return MemberRegisterResponse.of(registeredMember, stringEncoder)
    }

    private fun validDuplicatedLoginId(foundMember: Member?) {
        if (foundMember != null) {
            throw InterruptServerException(errorCode = ErrorCode.DUPLICATED_LOGIN_ID)
        }
    }

}