package com.interrupt.server.job.repository

import com.interrupt.server.job.dto.JobGroupDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class JobGroupRedisRepository(
    private val jobGroupRedisTemplate: RedisTemplate<String, JobGroupDto>
) {

    companion object {
        private const val KEY_PREFIX = "JOB_GROUP"
        private val TIMEOUT = Duration.ofDays(7)
    }

    fun save(jobGroupDto: JobGroupDto) =
        jobGroupRedisTemplate.opsForValue().set(generateKey(jobGroupDto.id.toString()), jobGroupDto, TIMEOUT)

    fun saveAll(jobGroupDtoList: List<JobGroupDto>) {
        jobGroupDtoList.associateBy { (generateKey(it.id.toString())) }
            .let { jobGroupRedisTemplate.opsForValue().multiSet(it) }
    }

    fun findAll(): List<JobGroupDto> =
        jobGroupRedisTemplate.keys("*").let {
            jobGroupRedisTemplate.opsForValue().multiGet(it).orEmpty()
        }

    private fun generateKey(key: String): String = "$KEY_PREFIX:$key"

}