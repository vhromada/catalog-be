package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeJokeRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for jokes.
 *
 * @author Vladimir Hromada
 */
interface JokeValidator {

    /**
     * Validates request for changing joke.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *
     * @param request request for changing joke
     * @throws InputException if request for changing joke isn't valid
     */
    fun validateRequest(request: ChangeJokeRequest)

}
