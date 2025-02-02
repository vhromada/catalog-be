package com.github.vhromada.catalog

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.common.auth.AuthContextFilter
import com.github.vhromada.catalog.common.log.LoggingFilter
import com.github.vhromada.catalog.common.log.SensitiveLog
import com.github.vhromada.catalog.mapper.IssueMapper
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * A class represents Spring configuration for web.
 *
 * @author Vladimir Hromada
 */
@Configuration
class CatalogWebConfiguration : WebMvcConfigurer {

    /**
     * Sensitive log rules
     */
    @Value("\${log.payload.sensitive}")
    private lateinit var sensitiveLogRules: String

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE")
    }

    /**
     * Returns filter for logging.
     *
     * @return filter for logging
     */
    @Bean
    fun loggingFilter(): FilterRegistrationBean<LoggingFilter> {
        val filter = FilterRegistrationBean<LoggingFilter>()
        filter.filter = LoggingFilter(sensitiveLog = SensitiveLog.of(rules = sensitiveLogRules))
        filter.addUrlPatterns("/rest/*")
        filter.order = 1
        return filter
    }

    /**
     * Returns filter for auth context.
     *
     * @param objectMapper object mapper
     * @param issueMapper  mapper between result and issues
     * @return filter for auth context
     */
    @Bean
    fun authContextFilter(objectMapper: ObjectMapper, issueMapper: IssueMapper): FilterRegistrationBean<AuthContextFilter> {
        val filter = FilterRegistrationBean<AuthContextFilter>()
        filter.filter = AuthContextFilter(objectMapper = objectMapper, issueMapper = issueMapper)
        filter.addUrlPatterns("/*")
        filter.order = 2
        return filter
    }

    /**
     * Returns swagger definition info.
     *
     * @return swagger definition info
     */
    @Bean
    fun catalogInfo(): OpenAPI {
        val info = Info()
            .title("Catalog")
            .description("Catalog of movies, shows, games, music, programs, books and jokes")
            .version("25.1")
        return OpenAPI()
            .info(info)
    }

}
