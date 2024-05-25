package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeShowRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for shows.
 *
 * @author Vladimir Hromada
 */
interface ShowValidator {

    /**
     * Validates request for changing show.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *
     * @param request request for changing show
     * @throws InputException if request for changing show isn't valid
     */
    fun validateRequest(request: ChangeShowRequest)

}
