package com.interrupt.server.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisCleanUp {

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    fun all() {
        val keys = redisTemplate.keys("*")
        redisTemplate.delete(keys)
    }
}
