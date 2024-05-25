package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeShowRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.Constants
import com.github.vhromada.catalog.validator.ShowValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for shows.
 *
 * @author Vladimir Hromada
 */
@Component("showValidator")
class ShowValidatorImpl : ShowValidator {

    override fun validateRequest(request: ChangeShowRequest) {
        val result = Result<Unit>()
        validateNames(request = request, result = result)
        if (request.imdbCode != null && (request.imdbCode < 1 || request.imdbCode > Constants.MAX_IMDB_CODE)) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "SHOW_IMDB_CODE_NOT_VALID", message = "IMDB code must be between 1 and 999999999."))
        }
        validateGenres(request = request, result = result)
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

    /**
     * Validates names.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *
     * @param request request for changing show
     * @param result  result with validation errors
     */
    private fun validateNames(request: ChangeShowRequest, result: Result<Unit>) {
        when {
            request.czechName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SHOW_CZECH_NAME_NULL", message = "Czech name mustn't be null."))
            }

            request.czechName.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SHOW_CZECH_NAME_EMPTY", message = "Czech name mustn't be empty string."))
            }
        }
        when {
            request.originalName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SHOW_ORIGINAL_NAME_NULL", message = "Original name mustn't be null."))
            }

            request.originalName.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SHOW_ORIGINAL_NAME_EMPTY", message = "Original name mustn't be empty string."))
            }
        }
    }

    /**
     * Validates genres.
     * <br></br>
     * Validation errors:
     *
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *
     * @param request request for changing show
     * @param result  result with validation errors
     */
    private fun validateGenres(request: ChangeShowRequest, result: Result<Unit>) {
        if (request.genres == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "SHOW_GENRES_NULL", message = "Genres mustn't be null."))
        } else {
            if (request.genres.contains(null)) {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SHOW_GENRES_CONTAIN_NULL", message = "Genres mustn't contain null value."))
            }
            for (genre in request.genres) {
                if (genre != null && genre.isBlank()) {
                    result.addEvent(event = Event(severity = Severity.ERROR, key = "SHOW_GENRE_EMPTY", message = "Genre mustn't be empty string."))
                }
            }
        }
    }

}
