package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.Credentials
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.AccountValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for accounts.
 *
 * @author Vladimir Hromada
 */
@Component("accountValidator")
class AccountValidatorImpl : AccountValidator {

    override fun validateCredentials(credentials: Credentials) {
        val result = Result<Unit>()
        when {
            credentials.username == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "CREDENTIALS_USERNAME_NULL", message = "Username mustn't be null."))
            }

            credentials.username.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "CREDENTIALS_USERNAME_EMPTY", message = "Username mustn't be empty string."))
            }
        }
        when {
            credentials.password == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "CREDENTIALS_PASSWORD_NULL", message = "Password mustn't be null."))
            }

            credentials.password.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "CREDENTIALS_PASSWORD_EMPTY", message = "Password mustn't be empty string."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
