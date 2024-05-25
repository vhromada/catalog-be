import com.adarshr.gradle.testlogger.theme.ThemeType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

plugins {
    val kotlinVersion = "1.9.24"
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("com.adarshr.test-logger") version "4.0.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    jacoco
}

group = "com.github.vhromada"
version = "24.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("io.github.oshai:kotlin-logging:6.0.9")
    implementation("commons-io:commons-io:2.16.1")
    implementation("com.github.openjson:openjson:1.0.13")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
        incremental = false
    }
}

tasks.withType<Test> {
    jvmArgs = listOf("-Xshare:off", "-XX:+EnableDynamicAgentLoading")
    useJUnitPlatform()
    testlogger {
        theme = ThemeType.STANDARD_PARALLEL
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<Jar> {
    manifest {
        attributes["Built-By"] = "Vladimir Hromada"
        attributes["Build-Timestamp"] = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        attributes["Implementation-Title"] = "Catalog BE"
        attributes["Implementation-Version"] = project.version
        attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
        attributes["Build-Jdk"] = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})"
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
