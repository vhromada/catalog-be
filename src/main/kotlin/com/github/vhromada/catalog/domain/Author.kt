package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents author.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "authors")
@Suppress("JpaDataSourceORMInspection")
data class Author(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "author_generator", sequenceName = "authors_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * First name
     */
    @Column(name = "first_name")
    var firstName: String,

    /**
     * Normalized first name
     */
    @Column(name = "normalized_first_name")
    var normalizedFirstName: String,

    /**
     * Middle name
     */
    @Column(name = "middle_name")
    var middleName: String?,

    /**
     * Normalized middle name
     */
    @Column(name = "normalized_middle_name")
    var normalizedMiddleName: String?,

    /**
     * Last name
     */
    @Column(name = "last_name")
    var lastName: String,

    /**
     * Normalized last name
     */
    @Column(name = "normalized_last_name")
    var normalizedLastName: String

) : Audit() {

    /**
     * Merges author.
     *
     * @param author author
     */
    fun merge(author: Author) {
        firstName = author.firstName
        normalizedFirstName = author.normalizedFirstName
        middleName = author.middleName
        normalizedMiddleName = author.normalizedMiddleName
        lastName = author.lastName
        normalizedLastName = author.normalizedLastName
    }

}
