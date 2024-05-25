package com.github.vhromada.catalog.facade

/**
 * An interface represents facade for roles.
 *
 * @author Vladimir Hromada
 */
interface RoleFacade {

    /**
     * Returns list of roles.
     *
     * @return list of roles
     */
    fun getAll(): List<String>

}
