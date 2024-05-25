package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents medium.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "media")
@Suppress("JpaDataSourceORMInspection")
data class Medium(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "medium_generator", sequenceName = "media_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medium_generator")
    var id: Int?,

    /**
     * Number
     */
    @Column(name = "medium_number")
    var number: Int,

    /**
     * Length
     */
    @Column(name = "medium_length")
    var length: Int

) : Audit()
