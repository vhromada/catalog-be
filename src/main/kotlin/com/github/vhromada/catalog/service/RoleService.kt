package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.domain.Role

/**
 * An interface represents service for roles.
 *
 * @author Vladimir Hromada
 */
interface RoleService {

    /**
     * Returns list of roles.
     *
     * @return list of roles
     */
    fun getAll(): List<Role>

    /**
     * Returns role.
     *
     * @param name name
     * @return role
     * @throws InputException if role doesn't exist in data storage
     */
    fun get(name: String): Role

}
