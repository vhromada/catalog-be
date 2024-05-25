package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.facade.RoleFacade
import com.github.vhromada.catalog.service.RoleService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for roles.
 *
 * @author Vladimir Hromada
 */
@Component("roleFacade")
class RoleFacadeImpl(

    /**
     * Service for roles
     */
    private val service: RoleService

) : RoleFacade {

    override fun getAll(): List<String> {
        return service.getAll().map { it.name }
    }

}