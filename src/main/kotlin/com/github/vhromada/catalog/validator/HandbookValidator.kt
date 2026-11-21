package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeHandbookRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for handbooks.
 *
 * @author Vladimir Hromada
 */
interface HandbookValidator {

    /**
     * Validates request for changing handbook.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *
     * @param request request for changing handbook
     * @throws InputException if request for changing handbook isn't valid
     */
    fun validateRequest(request: ChangeHandbookRequest)

}
