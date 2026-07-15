package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeBookRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.BookValidator
import com.github.vhromada.catalog.validator.utils.ValidationSupport
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for books.
 *
 * @author Vladimir Hromada
 */
@Component("bookValidator")
class BookValidatorImpl : BookValidator {

    override fun validateRequest(request: ChangeBookRequest) {
        val result = Result<Unit>()
        ValidationSupport.validateNames(czechName = request.czechName, originalName = request.originalName, prefix = "BOOK_", result = result)
        when {
            request.description == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_DESCRIPTION_NULL", message = "Description mustn't be null."))
            }

            request.description.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_DESCRIPTION_EMPTY", message = "Description mustn't be empty string."))
            }
        }
        ValidationSupport.validateItems(
            items = request.authors,
            collectionKey = "BOOK_AUTHORS",
            itemKey = "BOOK_AUTHOR",
            collectionLabel = "Authors",
            itemLabel = "Author",
            result = result
        )
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
