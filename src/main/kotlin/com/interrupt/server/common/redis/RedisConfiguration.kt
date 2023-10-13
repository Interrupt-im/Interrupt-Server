package com.interrupt.server.common.redis

import com.interrupt.server.member.entity.EmailVerifyCode
import com.interrupt.server.member.entity.MemberRecover
import io.lettuce.core.RedisURI
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories
class RedisConfiguration(
    private val redisProperties: RedisProperties
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisURI = RedisURI.create(redisProperties.host, redisProperties.port)
        val configuration: RedisConfiguration = LettuceConnectionFactory.createRedisConfiguration(redisURI)
        return LettuceConnectionFactory(configuration).also { it.afterPropertiesSet() }
    }

    @Bean(name = ["emailVerifyRedisTemplate"])
    fun emailVerifyRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, EmailVerifyCode> {
        return RedisTemplate<String, EmailVerifyCode>().apply {
            connectionFactory = redisConnectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = Jackson2JsonRedisSerializer(EmailVerifyCode::class.java)
        }
    }

    @Bean(name = ["memberRecoverRedisTemplate"])
    fun memberRecoverRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, MemberRecover> {
        return RedisTemplate<String, MemberRecover>().apply {
            connectionFactory = redisConnectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = Jackson2JsonRedisSerializer(MemberRecover::class.java)
        }
    }

}