package com.interrupt.server.career.job.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.interrupt.server.career.job.dto.JobGroupDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class JobGroupRedisRepository(
    private val jobGroupRedisTemplate: RedisTemplate<String, String>
) {

    companion object {
        private const val KEY_PREFIX = "JOB_GROUP"
        private val TIMEOUT = Duration.ofDays(7)
    }

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val valueOperations: ValueOperations<String, String>
        get() = this.jobGroupRedisTemplate.opsForValue()

    fun save(jobGroupDto: JobGroupDto) =
        valueOperations.set(
            generateKey(jobGroupDto.id.toString()),
            objectMapper.writeValueAsString(jobGroupDto),
            TIMEOUT)

    fun saveAll(jobGroupDtoList: List<JobGroupDto>) =
        jobGroupDtoList.associate { (generateKey(it.id.toString())) to objectMapper.writeValueAsString(it) }
            .let { valueOperations.multiSet(it) }

    fun findAll(): List<JobGroupDto> =
        jobGroupRedisTemplate.keys("$KEY_PREFIX*").let { keys ->
            valueOperations
                .multiGet(keys)
                .orEmpty()
                .map { objectMapper.readValue(it, JobGroupDto::class.java) }
                .sortedBy { it.id }
        }

    private fun generateKey(key: String): String = "$KEY_PREFIX:$key"

}