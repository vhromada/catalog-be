package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeRolesRequest
import com.github.vhromada.catalog.validator.RoleValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for roles.
 *
 * @author Vladimir Hromada
 */
@Component("roleValidator")
class RoleValidatorImpl : RoleValidator {

    override fun validateRequest(request: ChangeRolesRequest) {
        val result = Result<Unit>()
        when {
            request.roles == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "ROLE_ROLES_NULL", message = "Roles mustn't be null."))
            }

            request.roles.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "ROLE_ROLES_EMPTY", message = "Roles mustn't be empty."))
            }

            request.roles.contains(null) -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "ROLE_ROLES_CONTAIN_NULL", message = "Roles mustn't contain null value."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
