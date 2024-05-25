package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeProgramRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for programs.
 *
 * @author Vladimir Hromada
 */
interface ProgramValidator {

    /**
     * Validates request for changing program.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *  * Format is null
     *  * Crack is null
     *  * Serial key is null
     *
     * @param request request for changing program
     * @throws InputException if request for changing program isn't valid
     */
    fun validateRequest(request: ChangeProgramRequest)

}
