package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangePictureRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.PictureValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for pictures.
 *
 * @author Vladimir Hromada
 */
@Component("pictureValidator")
class PictureValidatorImpl : PictureValidator {

    override fun validateRequest(request: ChangePictureRequest) {
        val result = Result<Unit>()
        when {
            request.content == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "PICTURE_CONTENT_NULL", message = "Content mustn't be null."))
            }

            request.content.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "PICTURE_CONTENT_EMPTY", message = "Content mustn't be empty."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
