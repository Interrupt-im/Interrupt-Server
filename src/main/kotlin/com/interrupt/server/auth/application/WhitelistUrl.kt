package com.interrupt.server.auth.application

import com.interrupt.server.global.util.matchUrl
import com.interrupt.server.member.domain.MemberType
import org.springframework.http.HttpMethod

data class WhitelistUrl(
    val url: String,
    val methods: List<HttpMethod> = listOf(),
    val role: MemberType = MemberType.CUSTOMER,
) {
    fun match(uri: String, method: String): Boolean = isMatchUri(uri) && isMatchMethod(method)

    private fun isMatchMethod(method: String): Boolean {
        val httpMethod = HttpMethod.valueOf(method)

        return methods.isEmpty() || methods.any { it == httpMethod }
    }

    private fun isMatchUri(uri: String): Boolean = matchUrl(this.url, uri)
}
