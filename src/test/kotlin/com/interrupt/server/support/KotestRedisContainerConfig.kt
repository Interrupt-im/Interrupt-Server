package com.interrupt.server.support

import io.kotest.core.config.AbstractProjectConfig
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

object KotestRedisContainerConfig : AbstractProjectConfig() {

    private const val TEST_REDIS_CONTAINER_IMAGE = "redis:7.4.1"
    private const val TEST_REDIS_CONTAINER_PORT = 6379

    override suspend fun beforeProject() {
        val redis = GenericContainer(DockerImageName.parse(TEST_REDIS_CONTAINER_IMAGE))
            .withExposedPorts(TEST_REDIS_CONTAINER_PORT)

        redis.start()

        System.setProperty("spring.data.redis.host", redis.host)
        System.setProperty("spring.data.redis.port", redis.getMappedPort(TEST_REDIS_CONTAINER_PORT).toString())

        super.beforeProject()
    }
}
