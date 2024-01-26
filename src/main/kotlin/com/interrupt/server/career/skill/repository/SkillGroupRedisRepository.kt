package com.interrupt.server.career.skill.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.interrupt.server.career.skill.dto.SkillGroupDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class SkillGroupRedisRepository(
    private val skillGroupRedisTemplate: RedisTemplate<String, String>
) {

    companion object {
        private const val KEY_PREFIX = "SKILL_GROUP"
        private val TIMEOUT = Duration.ofDays(7)
    }

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val valueOperations: ValueOperations<String, String>
        get() = this.skillGroupRedisTemplate.opsForValue()

    fun save(skillGroupDto: SkillGroupDto) =
        valueOperations.set(
            generateKey(skillGroupDto.id.toString()),
            objectMapper.writeValueAsString(skillGroupDto),
            TIMEOUT)

    fun saveAll(skillGroupDtoList: List<SkillGroupDto>) =
        skillGroupDtoList
            .associate { (generateKey(it.id.toString())) to objectMapper.writeValueAsString(it) }
            .let { valueOperations.multiSet(it) }


    fun findAll(): List<SkillGroupDto> =
        skillGroupRedisTemplate.keys("$KEY_PREFIX*").let { keys ->
            valueOperations
                .multiGet(keys)
                .orEmpty()
                .map { objectMapper.readValue(it, SkillGroupDto::class.java) }
                .sortedBy { it.id }
        }

    private fun generateKey(key: String): String = "$KEY_PREFIX:$key"

}