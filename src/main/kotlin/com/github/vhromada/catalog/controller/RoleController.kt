package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.facade.RoleFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * A class represents controller for credentials.
 *
 * @author Vladimir Hromada
 */
@RestController("roleController")
@RequestMapping("rest/roles")
@Tag(name = "Roles")
class RoleController(

    /**
     * Facade for roles
     */
    private val facade: RoleFacade

) {

    /**
     * Returns list of roles.
     *
     * @return list of roles
     */
    @GetMapping
    fun getAll(): List<String> {
        return facade.getAll()
    }

}
