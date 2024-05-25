package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeAuthorRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.AuthorValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for authors.
 *
 * @author Vladimir Hromada
 */
@Component("authorValidator")
class AuthorValidatorImpl : AuthorValidator {

    override fun validateRequest(request: ChangeAuthorRequest) {
        val result = Result<Unit>()
        when {
            request.firstName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "AUTHOR_FIRST_NAME_NULL", message = "First name mustn't be null."))
            }

            request.firstName.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "AUTHOR_FIRST_NAME_EMPTY", message = "First name mustn't be empty string."))
            }

            request.lastName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "AUTHOR_LAST_NAME_NULL", message = "Last name mustn't be null."))
            }

            request.lastName.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "AUTHOR_LAST_NAME_EMPTY", message = "Last name mustn't be empty string."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
