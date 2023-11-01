package com.interrupt.server.career.skill.repository

import com.interrupt.server.career.skill.dto.SkillGroupDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class SkillGroupRedisRepository(
    private val skillGroupRedisTemplate: RedisTemplate<String, SkillGroupDto>
) {

    companion object {
        private const val KEY_PREFIX = "SKILL_GROUP"
        private val TIMEOUT = Duration.ofDays(7)
    }

    fun save(skillGroupDto: SkillGroupDto) =
        skillGroupRedisTemplate.opsForValue().set(generateKey(skillGroupDto.id.toString()), skillGroupDto, TIMEOUT)

    fun saveAll(skillGroupDtoList: List<SkillGroupDto>) {
        skillGroupDtoList.associateBy { (generateKey(it.id.toString())) }
            .let { skillGroupRedisTemplate.opsForValue().multiSet(it) }
    }

    fun findAll(): List<SkillGroupDto> =
        skillGroupRedisTemplate.keys("*").let {
            skillGroupRedisTemplate.opsForValue().multiGet(it).orEmpty()
        }

    private fun generateKey(key: String): String = "$KEY_PREFIX:$key"

}