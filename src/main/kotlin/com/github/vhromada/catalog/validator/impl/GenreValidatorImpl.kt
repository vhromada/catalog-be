package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.GenreValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for genres.
 *
 * @author Vladimir Hromada
 */
@Component("genreValidator")
class GenreValidatorImpl : GenreValidator {

    override fun validateRequest(request: ChangeGenreRequest) {
        val result = Result<Unit>()
        when {
            request.name == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "GENRE_NAME_NULL", message = "Name mustn't be null."))
            }

            request.name.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "GENRE_NAME_EMPTY", message = "Name mustn't be empty string."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
