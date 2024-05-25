package com.github.vhromada.catalog.entity

/**
 * A class represents author.
 *
 * @author Vladimir Hromada
 */
data class Author(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * First name
     */
    val firstName: String,

    /**
     * Middle name
     */
    val middleName: String?,

    /**
     * Last name
     */
    val lastName: String

)
