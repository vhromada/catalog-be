package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeMovieRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.Constants
import com.github.vhromada.catalog.validator.MovieValidator
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
        validateNames(request = request, result = result)
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
     * @param request request for changing movie
     * @param result  result with validation errors
     */
    private fun validateNames(request: ChangeMovieRequest, result: Result<Unit>) {
        when {
            request.czechName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_CZECH_NAME_NULL", message = "Czech name mustn't be null."))
            }

            request.czechName.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_CZECH_NAME_EMPTY", message = "Czech name mustn't be empty string."))
            }
        }
        when {
            request.originalName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_ORIGINAL_NAME_NULL", message = "Original name mustn't be null."))
            }

            request.originalName.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_ORIGINAL_NAME_EMPTY", message = "Original name mustn't be empty string."))
            }
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
            for (medium in request.media) {
                if (medium != null && medium <= 0) {
                    result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_MEDIUM_NOT_POSITIVE", message = "Medium must be positive number."))
                }
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
     * @param request request for changing movie
     * @param result  result with validation errors
     */
    private fun validateGenres(request: ChangeMovieRequest, result: Result<Unit>) {
        if (request.genres == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_GENRES_NULL", message = "Genres mustn't be null."))
        } else {
            if (request.genres.contains(null)) {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_GENRES_CONTAIN_NULL", message = "Genres mustn't contain null value."))
            }
            for (genre in request.genres) {
                if (genre != null && genre.isBlank()) {
                    result.addEvent(event = Event(severity = Severity.ERROR, key = "MOVIE_GENRE_EMPTY", message = "Genre mustn't be empty string."))
                }
            }
        }
    }

}
