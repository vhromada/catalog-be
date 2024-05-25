package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeAuthorRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for authors.
 *
 * @author Vladimir Hromada
 */
interface AuthorValidator {

    /**
     * Validates request for changing author.
     * <br></br>
     * Validation errors:
     *
     *  * First name is null
     *  * First name is empty string
     *  * Last name is null
     *  * Last name is empty string
     *
     * @param request request for changing author
     * @throws InputException if request for changing author isn't valid
     */
    fun validateRequest(request: ChangeAuthorRequest)

}
