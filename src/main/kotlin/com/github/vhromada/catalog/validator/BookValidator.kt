package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeBookRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for books.
 *
 * @author Vladimir Hromada
 */
interface BookValidator {

    /**
     * Validates request for changing book.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Description is null
     *  * Description is empty string
     *  * Authors are null
     *  * Authors contain null value
     *  * Authors is empty string
     *
     * @param request request for changing book
     * @throws InputException if request for changing book isn't valid
     */
    fun validateRequest(request: ChangeBookRequest)

}
