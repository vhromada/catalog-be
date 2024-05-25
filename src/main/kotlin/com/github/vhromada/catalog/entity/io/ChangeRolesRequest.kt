package com.github.vhromada.catalog.entity.io

/**
 * A class represents request for changing roles.
 *
 * @author Vladimir Hromada
 */
data class ChangeRolesRequest(

    /**
     * Roles
     */
    val roles: List<String?>?

)
