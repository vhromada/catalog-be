package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeMovieRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.Constants
import com.github.vhromada.catalog.validator.MovieValidator
import com.github.vhromada.catalog.validator.utils.ValidationSupport
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for movies.
 *
 * @author Vladimir Hromada
 */
@Component("movieValidator")
class MovieValidatorImpl : MovieValidator {

    override fun validateRequest(request: ChangeMovieRequest) {
        val result = Result<Unit>()
        ValidationSupport.validateNames(czechName = request.czechName, originalName = request.originalName, prefix = "MOVIE_", result = result)
        when {
            request.year == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_YEAR_NULL", message = "Year mustn't be null."))
            }

            request.year < Constants.MIN_YEAR || request.year > Constants.CURRENT_YEAR -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_YEAR_NOT_VALID", message = "Year must be between ${Constants.MIN_YEAR} and ${Constants.CURRENT_YEAR}."))
            }
        }
        validateLanguages(request = request, result = result)
        validateMedia(request = request, result = result)
        if (request.imdbCode != null && (request.imdbCode < 1 || request.imdbCode > Constants.MAX_IMDB_CODE)) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_IMDB_CODE_NOT_VALID", message = "IMDB code must be between 1 and 999999999."))
        }
        ValidationSupport.validateItems(
            items = request.genres,
            collectionKey = "MOVIE_GENRES",
            itemKey = "MOVIE_GENRE",
            collectionLabel = "Genres",
            itemLabel = "Genre",
            result = result
        )
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

    /**
     * Validates languages.
     * <br></br>
     * Validation errors:
     *
     *  * Languages are null
     *  * Languages are empty
     *  * Languages contain null value
     *  * Subtitles are null
     *  * Subtitles contain null value
     *
     * @param request request for changing movie
     * @param result  result with validation errors
     */
    private fun validateLanguages(request: ChangeMovieRequest, result: Result<Unit>) {
        when {
            request.languages == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_LANGUAGES_NULL", message = "Languages mustn't be null."))
            }

            request.languages.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_LANGUAGES_EMPTY", message = "Languages mustn't be empty."))
            }

            request.languages.contains(null) -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_LANGUAGES_CONTAIN_NULL", message = "Languages mustn't contain null value."))
            }
        }
        when {
            request.subtitles == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_SUBTITLES_NULL", message = "Subtitles mustn't be null."))
            }

            request.subtitles.contains(null) -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_SUBTITLES_CONTAIN_NULL", message = "Subtitles mustn't contain null value."))
            }
        }
    }

    /**
     * Validates media.
     * <br></br>
     * Validation errors:
     *
     *  * Media are null
     *  * Media contain null value
     *  * Medium is negative value
     *
     * @param request request for changing movie
     * @param result  result with validation errors
     */
    private fun validateMedia(request: ChangeMovieRequest, result: Result<Unit>) {
        if (request.media == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_MEDIA_NULL", message = "Media mustn't be null."))
        } else {
            if (request.media.contains(null)) {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_MEDIA_CONTAIN_NULL", message = "Media mustn't contain null value."))
            }
            request.media.filterNotNull().forEach { medium ->
                if (medium <= 0) {
                    result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_MEDIUM_NOT_POSITIVE", message = "Medium must be positive number."))
                }
            }
        }
    }

}
