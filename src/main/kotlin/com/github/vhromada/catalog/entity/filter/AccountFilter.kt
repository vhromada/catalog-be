package com.github.vhromada.catalog.entity.filter

import com.github.vhromada.catalog.common.FieldOperation

/**
 * A class represents filter for accounts.
 *
 * @author Vladimir Hromada
 */
data class AccountFilter(

    /**
     * UUID
     */
    val uuid: String? = null,

    /**
     * Username
     */
    val username: String? = null,

    /**
     * Operation for username
     */
    val usernameOperation: FieldOperation? = null

) : PagingFilter()
