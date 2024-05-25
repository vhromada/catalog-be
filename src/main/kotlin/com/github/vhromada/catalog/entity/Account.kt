package com.github.vhromada.catalog.entity

/**
 * A class represents account.
 *
 * @author Vladimir Hromada
 */
data class Account(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Username
     */
    val username: String,

    /**
     * True if account is locked
     */
    val locked: Boolean,

    /**
     * Roles
     */
    val roles: List<String>

)
