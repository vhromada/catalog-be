package com.github.vhromada.catalog.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

/**
 * A class represents register.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "registers")
@Suppress("JpaDataSourceORMInspection")
data class Register(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "register_generator", sequenceName = "registers_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "register_generator")
    var id: Int?,

    /**
     * Number
     */
    @Column(name = "register_number")
    val number: Int,

    /**
     * Name
     */
    @Column(name = "register_name")
    val name: String,

    /**
     * Values
     */
    @OneToMany(mappedBy = "register", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("order")
    @Fetch(FetchMode.SELECT)
    val values: List<RegisterValue>

)
