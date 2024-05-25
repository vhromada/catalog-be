package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeBookItemRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.BookItemValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for book items.
 *
 * @author Vladimir Hromada
 */
@Component("bookItemValidator")
class BookItemValidatorImpl : BookItemValidator {

    override fun validateRequest(request: ChangeBookItemRequest) {
        val result = Result<Unit>()
        validateLanguages(request = request, result = result)
        if (request.format == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_ITEM_FORMAT_NULL", message = "Format mustn't be null."))
        }
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
     *  * Languages contain null value
     *
     * @param request request for changing book item
     * @param result  result with validation errors
     */
    private fun validateLanguages(request: ChangeBookItemRequest, result: Result<Unit>) {
        when {
            request.languages == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_ITEM_LANGUAGES_NULL", message = "Languages mustn't be null."))
            }

            request.languages.contains(null) -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_ITEM_LANGUAGES_CONTAIN_NULL", message = "Languages mustn't contain null value."))
            }
        }
    }

}
