package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeWalkthroughRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for walkthroughs.
 *
 * @author Vladimir Hromada
 */
interface WalkthroughValidator {

    /**
     * Validates request for changing walkthrough.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *
     * @param request request for changing walkthrough
     * @throws InputException if request for changing walkthrough isn't valid
     */
    fun validateRequest(request: ChangeWalkthroughRequest)

}
