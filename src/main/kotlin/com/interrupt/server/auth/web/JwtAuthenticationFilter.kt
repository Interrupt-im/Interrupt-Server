package com.interrupt.server.auth.web

import com.interrupt.server.auth.repository.TokenRedisRepository
import com.interrupt.server.auth.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val tokenRedisRepository: TokenRedisRepository,
): OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        if (request.servletPath.contains("/api/v1/auth")) {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        val username = jwtService.getUsername(jwt)
        val jti = jwtService.getJtl(jwt)
        val authenticationCredentials = tokenRedisRepository.findById(jti)

        if (authenticationCredentials?.credentials?.accessToken == jwt && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)

            if (jwtService.isTokenValid(jwt, userDetails)) {
                updateContext(userDetails, request)
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun updateContext(userDetails: UserDetails, request: HttpServletRequest) =
        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            .also {
                it.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = it
            }

}