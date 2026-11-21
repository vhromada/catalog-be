package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeHandbookRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.HandbookValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for handbooks.
 *
 * @author Vladimir Hromada
 */
@Component("handbookValidator")
class HandbookValidatorImpl : HandbookValidator {

    override fun validateRequest(request: ChangeHandbookRequest) {
        val result = Result<Unit>()
        when {
            request.content == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "HANDBOOK_CONTENT_NULL", message = "Content mustn't be null."))
            }

            request.content.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "HANDBOOK_CONTENT_EMPTY", message = "Content mustn't be empty string."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
