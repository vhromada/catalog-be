package com.github.vhromada.catalog

import com.github.vhromada.catalog.provider.TimeProvider
import com.github.vhromada.catalog.service.NormalizerService
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.PropertySource
import org.springframework.data.domain.AuditorAware
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.Optional

/**
 * A class represents Spring configuration for tests.
 *
 * @author Vladimir Hromada
 */
@Import(CatalogConfiguration::class, ContainerConfiguration::class)
@PropertySource("classpath:catalog-test.properties")
class TestConfiguration {

    /**
     * Returns UUID of user.
     *
     * @return UUID of user
     */
    @Bean
    @Primary
    fun userAuditor(): AuditorAware<String> {
        return AuditorAware { Optional.of(AccountUtils.getDomainAccount(index = 2).uuid!!) }
    }

    /**
     * Returns provider for time.
     *
     * @return provider for time
     */
    @Bean
    @Primary
    fun timeProvider(): TimeProvider {
        return object : TimeProvider {
            override fun getTime(): LocalDateTime {
                return TestConstants.TIME
            }
        }
    }

    /**
     * Returns password encoder.
     *
     * @return password encoder
     */
    @Bean
    @Primary
    fun passwordEncoder(): PasswordEncoder {
        return object : PasswordEncoder {
            override fun encode(rawPassword: CharSequence?): String {
                return rawPassword.toString()
            }

            override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
                return rawPassword.toString() == encodedPassword
            }
        }
    }

    /**
     * Returns service for normalizing strings.
     *
     * @return service for normalizing strings
     */
    @Bean
    @Primary
    fun normalizerService(): NormalizerService {
        return object : NormalizerService {
            override fun normalize(source: String): String {
                return source
            }
        }
    }

}
