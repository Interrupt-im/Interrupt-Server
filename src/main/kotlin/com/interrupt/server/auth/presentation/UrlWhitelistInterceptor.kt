package com.interrupt.server.auth.presentation

import com.interrupt.server.auth.application.WhitelistUrl
import com.interrupt.server.global.exception.ApplicationException
import com.interrupt.server.global.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

class UrlWhitelistInterceptor(
    private vararg val whitelistUrls: WhitelistUrl = arrayOf()
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val uri = request.requestURI
        val method = request.method

        if (whitelistUrls.any { it.match(uri, method) }) {
            return true
        }

        throw ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS_ATTEMPT)
    }
}
