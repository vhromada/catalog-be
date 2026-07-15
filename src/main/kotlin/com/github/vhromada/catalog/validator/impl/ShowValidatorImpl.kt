package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeShowRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.Constants
import com.github.vhromada.catalog.validator.ShowValidator
import com.github.vhromada.catalog.validator.utils.ValidationSupport
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
        ValidationSupport.validateNames(czechName = request.czechName, originalName = request.originalName, prefix = "SHOW_", result = result)
        if (request.imdbCode != null && (request.imdbCode < 1 || request.imdbCode > Constants.MAX_IMDB_CODE)) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "SHOW_IMDB_CODE_NOT_VALID", message = "IMDB code must be between 1 and 999999999."))
        }
        ValidationSupport.validateItems(
            items = request.genres,
            collectionKey = "SHOW_GENRES",
            itemKey = "SHOW_GENRE",
            collectionLabel = "Genres",
            itemLabel = "Genre",
            result = result
        )
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
