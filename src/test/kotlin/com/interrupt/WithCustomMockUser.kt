package com.interrupt

import com.interrupt.server.WithCustomMockUserSecurityContextFactory
import com.interrupt.server.member.entity.MemberRole
import org.springframework.security.test.context.support.WithSecurityContext

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory::class)
annotation class WithCustomMockUser(
    val loginId: String = "loginId",
    val password: String = "password",
    val name: String = "name",
    val email: String = "email@interrupt.im",
    val role: MemberRole = MemberRole.USER
) {
}
