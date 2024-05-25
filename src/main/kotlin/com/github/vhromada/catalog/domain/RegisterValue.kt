package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents register's value.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "register_values")
@Suppress("JpaDataSourceORMInspection")
data class RegisterValue(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "register_value_generator", sequenceName = "register_values_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "register_value_generator")
    var id: Int?,

    /**
     * Code
     */
    @Column(name = "register_code")
    val code: String,

    /**
     * Order
     */
    @Column(name = "register_order")
    val order: Int,

    /**
     * Register
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "register")
    var register: Register? = null

) {

    override fun toString(): String {
        return "RegisterValue(id=$id, code=$code, order=$order, register=${register?.id})"
    }

}
