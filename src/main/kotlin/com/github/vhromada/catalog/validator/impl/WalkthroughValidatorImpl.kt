package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeWalkthroughRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.WalkthroughValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for walkthroughs.
 *
 * @author Vladimir Hromada
 */
@Component("walkthroughValidator")
class WalkthroughValidatorImpl : WalkthroughValidator {

    override fun validateRequest(request: ChangeWalkthroughRequest) {
        val result = Result<Unit>()
        when {
            request.content == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "WALKTHROUGH_CONTENT_NULL", message = "Content mustn't be null."))
            }

            request.content.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "WALKTHROUGH_CONTENT_EMPTY", message = "Content mustn't be empty string."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
