package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeBookRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.BookValidator
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
        validateNames(request = request, result = result)
        when {
            request.description == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_DESCRIPTION_NULL", message = "Description mustn't be null."))
            }

            request.description.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_DESCRIPTION_EMPTY", message = "Description mustn't be empty string."))
            }
        }
        validateAuthors(request = request, result = result)
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
     * @param request request for changing book
     * @param result  result with validation errors
     */
    private fun validateNames(request: ChangeBookRequest, result: Result<Unit>) {
        when {
            request.czechName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_CZECH_NAME_NULL", message = "Czech name mustn't be null."))
            }

            request.czechName.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_CZECH_NAME_EMPTY", message = "Czech name mustn't be empty string."))
            }
        }
        when {
            request.originalName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_ORIGINAL_NAME_NULL", message = "Original name mustn't be null."))
            }

            request.originalName.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_ORIGINAL_NAME_EMPTY", message = "Original name mustn't be empty string."))
            }
        }
    }

    /**
     * Validates authors.
     * <br></br>
     * Validation errors:
     *
     *  * Authors are null
     *  * Authors contain null value
     *  * Author is empty string
     *
     * @param request request for changing book
     * @param result  result with validation errors
     */
    private fun validateAuthors(request: ChangeBookRequest, result: Result<Unit>) {
        if (request.authors == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_AUTHORS_NULL", message = "Authors mustn't be null."))
        } else {
            if (request.authors.contains(null)) {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_AUTHORS_CONTAIN_NULL", message = "Authors mustn't contain null value."))
            }
            for (author in request.authors) {
                if (author != null && author.isBlank()) {
                    result.addEvent(event = Event(severity = Severity.ERROR, key = "BOOK_AUTHOR_EMPTY", message = "Author mustn't be empty string."))
                }
            }
        }
    }

}
