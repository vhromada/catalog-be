package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.entity.io.ChangeRolesRequest

/**
 * An interface represents validator for roles.
 *
 * @author Vladimir Hromada
 */
interface RoleValidator {

    /**
     * Validates request for changing roles.
     * <br></br>
     * Validation errors:
     *
     *  * Roles is null
     *  * Roles is empty
     *  * Roles contains null
     *
     * @param request request for changing roles
     * @throws InputException if request for changing roles isn't valid
     */
    fun validateRequest(request: ChangeRolesRequest)

}