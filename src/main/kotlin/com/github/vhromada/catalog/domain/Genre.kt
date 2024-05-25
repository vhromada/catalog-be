package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents genre.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "genres")
@Suppress("JpaDataSourceORMInspection")
data class Genre(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "genre_generator", sequenceName = "genres_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genre_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    @Column(name = "genre_name")
    var name: String,

    /**
     * Normalized name
     */
    @Column(name = "normalized_genre_name")
    var normalizedName: String

) : Audit() {

    /**
     * Merges genre.
     *
     * @param genre genre
     */
    fun merge(genre: Genre) {
        name = genre.name
        normalizedName = genre.normalizedName
    }

}
