import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.9.0"

    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("com.interrupt.server.common.redis.RedisEntity")
}

group = "com.interrupt"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // DB
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.mockk:mockk:1.13.8")

    // 시큐리티
//    implementation("org.springframework.boot:spring-boot-starter-security")
//    testImplementation("org.springframework.security:spring-security-test")

    // 로깅
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

    // 메일
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // 타임리프
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 코루틴
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.6.0")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
