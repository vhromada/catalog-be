package com.github.vhromada.catalog

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

/**
 * A class represents Spring configuration for test container.
 *
 * @author Vladimir Hromada
 */
@Configuration
class ContainerConfiguration {

    /**
     * Returns test container for PostgreSQL.
     *
     * @return test container for PostgreSQL
     */
    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("postgres:15.7"))
    }

}
