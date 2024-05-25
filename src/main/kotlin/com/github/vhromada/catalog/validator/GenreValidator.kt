package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreValidator {

    /**
     * Validates request for changing genre.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *
     * @param request request for changing genre
     * @throws InputException if request for changing genre isn't valid
     */
    fun validateRequest(request: ChangeGenreRequest)

}
