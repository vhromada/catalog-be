package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.Credentials
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for accounts.
 *
 * @author Vladimir Hromada
 */
interface AccountValidator {

    /**
     * Validates credentials.
     * <br></br>
     * Validation errors:
     *
     *  * Username is null
     *  * Username is empty string
     *  * Password is null
     *  * Password is empty string
     *
     * @param credentials credentials
     * @throws InputException if credentials aren't valid
     */
    fun validateCredentials(credentials: Credentials)

}
