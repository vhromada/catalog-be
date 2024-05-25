package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeMusicRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.MusicValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for music.
 *
 * @author Vladimir Hromada
 */
@Component("musicValidator")
class MusicValidatorImpl : MusicValidator {

    override fun validateRequest(request: ChangeMusicRequest) {
        val result = Result<Unit>()
        when {
            request.name == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MUSIC_NAME_NULL", message = "Name mustn't be null."))
            }

            request.name.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MUSIC_NAME_EMPTY", message = "Name mustn't be empty string."))
            }
        }
        when {
            request.mediaCount == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MUSIC_MEDIA_COUNT_NULL", message = "Count of media mustn't be null."))
            }

            request.mediaCount <= 0 -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "MUSIC_MEDIA_COUNT_NOT_POSITIVE", message = "Count of media must be positive number."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
