package com.interrupt.server.member.application

import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode
import com.interrupt.server.member.application.command.MemberCreateCommand
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberCommandService(
    private val memberCommandRepository: MemberCommandRepository,
    private val memberQueryRepository: MemberQueryRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun createMember(memberCreateCommand: MemberCreateCommand): Long {
        if (isAlreadyExistEmail(memberCreateCommand.email)) {
            throw ApplicationException(ErrorCode.DUPLICATED_REGISTER_EMAIL)
        }

        val member = memberCreateCommand.toEntity(passwordEncoder)

        return memberCommandRepository.save(member).id
    }

    private fun isAlreadyExistEmail(email: String?): Boolean = memberQueryRepository.existsByEmailAndNotDeleted(email)
}
