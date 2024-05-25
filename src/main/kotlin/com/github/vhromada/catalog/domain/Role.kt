package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents role.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "roles")
@Suppress("JpaDataSourceORMInspection")
data class Role(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "role_generator", sequenceName = "roles_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_generator")
    val id: Int?,

    /**
     * Name
     */
    @Column(name = "role_name")
    val name: String

)
