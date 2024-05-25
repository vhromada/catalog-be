package com.github.vhromada.catalog

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * A class represents Spring boot application.
 *
 * @author Vladimir Hromada
 */
@SpringBootApplication
class CatalogApplication

fun main(args: Array<String>) {
    SpringApplication.run(CatalogApplication::class.java, *args)
}
