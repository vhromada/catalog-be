package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeMovieRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for movies.
 *
 * @author Vladimir Hromada
 */
interface MovieValidator {

    /**
     * Validates request for changing movie.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Year is null
     *  * Year isn't between 1930 and current year
     *  * Languages are null
     *  * Languages are empty
     *  * Languages contain null value
     *  * Subtitles are null
     *  * Subtitles contain null value
     *  * Media are null
     *  * Media contain null value
     *  * Medium is negative value
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *
     * @param request request for changing movie
     * @throws InputException if request for changing movie isn't valid
     */
    fun validateRequest(request: ChangeMovieRequest)

}
