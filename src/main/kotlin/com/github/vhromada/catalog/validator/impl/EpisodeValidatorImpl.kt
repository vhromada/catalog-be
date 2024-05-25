package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeEpisodeRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.EpisodeValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for episodes.
 *
 * @author Vladimir Hromada
 */
@Component("episodeValidator")
class EpisodeValidatorImpl : EpisodeValidator {

    override fun validateRequest(request: ChangeEpisodeRequest) {
        val result = Result<Unit>()
        when {
            request.number == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "EPISODE_NUMBER_NULL", message = "Number of episode mustn't be null."))
            }

            request.number <= 0 -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "EPISODE_NUMBER_NOT_POSITIVE", message = "Number of episode must be positive number."))
            }
        }
        when {
            request.name == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "EPISODE_NAME_NULL", message = "Name mustn't be null."))
            }

            request.name.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "EPISODE_NAME_EMPTY", message = "Name mustn't be empty string."))
            }
        }
        when {
            request.length == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "EPISODE_LENGTH_NULL", message = "Length of episode mustn't be null."))
            }

            request.length < 0 -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "EPISODE_LENGTH_NEGATIVE", message = "Length of episode mustn't be negative number."))
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
