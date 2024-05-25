package com.github.vhromada.catalog

import com.github.vhromada.catalog.common.auth.AuthContextHolder
import com.github.vhromada.catalog.common.auth.Header
import com.github.vhromada.catalog.provider.TimeProvider
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

/**
 * A class represents Spring configuration for catalog.
 *
 * @author Vladimir Hromada
 */
@Configuration
@ComponentScan("com.github.vhromada.catalog")
@EnableJpaRepositories("com.github.vhromada.catalog.repository")
@EntityScan("com.github.vhromada.catalog.domain")
@EnableJpaAuditing(auditorAwareRef = "userAuditor", dateTimeProviderRef = "dateTimeProvider")
class CatalogConfiguration {

    /**
     * Returns UUID of user.
     *
     * @return UUID of user
     */
    @Bean
    fun userAuditor(): AuditorAware<String> {
        return AuditorAware {
            val user = AuthContextHolder.get().getHeader(name = Header.USER)
            return@AuditorAware if (user.isNullOrBlank()) {
                Optional.of("technical")
            } else {
                Optional.of(user)
            }
        }
    }

    /**
     * Returns current time.
     *
     * @param timeProvider provider for time
     * @return current time
     */
    @Bean
    fun dateTimeProvider(timeProvider: TimeProvider): DateTimeProvider {
        return DateTimeProvider { Optional.of(timeProvider.getTime()) }
    }

    /**
     * Returns password encoder.
     *
     * @return password encoder
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

}
