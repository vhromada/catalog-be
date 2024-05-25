package com.github.vhromada.catalog

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

/**
 * A class represents Spring configuration for security.
 *
 * @author Vladimir Hromada
 */
@Configuration
@EnableWebSecurity
class CatalogSecurityConfiguration {

    /**
     * Password encoder
     */
    @Autowired
    private lateinit var encoder: PasswordEncoder

    /**
     * Returns in memory user service.
     *
     * @return in memory user service
     */
    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User.withUsername("catalog")
            .password(encoder.encode("catalog"))
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }

    /**
     * Returns filter chain.
     *
     * @param http HTTP security
     * @return filter chain
     */
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers("/rest/public/**", "/app", "/app/*").permitAll()
                .anyRequest().authenticated()
        }
        http.httpBasic {}
        http.csrf { it.disable() }
        return http.build()
    }

}
