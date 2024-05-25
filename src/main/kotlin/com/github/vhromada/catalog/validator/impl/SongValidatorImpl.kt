package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeSongRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.SongValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for songs.
 *
 * @author Vladimir Hromada
 */
@Component("songValidator")
class SongValidatorImpl : SongValidator {

    override fun validateRequest(request: ChangeSongRequest) {
        val result = Result<Unit>()
        when {
            request.name == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SONG_NAME_NULL", message = "Name mustn't be null."))
            }

            request.name.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SONG_NAME_EMPTY", message = "Name mustn't be empty string."))
            }
        }
        when {
            request.length == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SONG_LENGTH_NULL", message = "Length of song mustn't be null."))
            }

            request.length < 0 -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SONG_LENGTH_NEGATIVE", message = "Length of song mustn't be negative number."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
