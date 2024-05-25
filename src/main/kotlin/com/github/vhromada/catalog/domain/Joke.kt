package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents joke.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "jokes")
@Suppress("JpaDataSourceORMInspection")
data class Joke(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "joke_generator", sequenceName = "jokes_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "joke_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Content
     */
    @Column(name = "content")
    var content: String

) : Audit() {

    /**
     * Merges joke.
     *
     * @param joke joke
     */
    fun merge(joke: Joke) {
        content = joke.content
    }

}
