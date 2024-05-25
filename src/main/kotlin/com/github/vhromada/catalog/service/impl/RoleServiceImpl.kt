package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.domain.Role
import com.github.vhromada.catalog.repository.RoleRepository
import com.github.vhromada.catalog.service.RoleService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

/**
 * A class represents implementation of service for roles.
 *
 * @author Vladimir Hromada
 */
@Service("roleService")
class RoleServiceImpl(

    /**
     * Repository for roles
     */
    private val repository: RoleRepository

) : RoleService {

    override fun getAll(): List<Role> {
        return repository.findAll()
    }

    override fun get(name: String): Role {
        return repository.findByName(name = name)
            .orElseThrow { InputException(key = "ROLE_NOT_EXIST", message = "Role doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

}
