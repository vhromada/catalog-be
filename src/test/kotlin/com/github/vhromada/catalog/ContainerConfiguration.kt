package com.github.vhromada.catalog

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

/**
 * A class represents Spring configuration for test container.
 *
 * @author Vladimir Hromada
 */
@TestConfiguration(proxyBeanMethods = false)
class ContainerConfiguration {

    /**
     * Returns test container for PostgreSQL.
     *
     * @return test container for PostgreSQL
     */
    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer {
        return PostgreSQLContainer(DockerImageName.parse("postgres:18.3"))
    }

}
