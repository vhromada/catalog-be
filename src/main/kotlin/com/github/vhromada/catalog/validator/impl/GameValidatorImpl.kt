package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeGameRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.GameValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for games.
 *
 * @author Vladimir Hromada
 */
@Component("gameValidator")
class GameValidatorImpl : GameValidator {

    override fun validateRequest(request: ChangeGameRequest) {
        val result = Result<Unit>()
        when {
            request.name == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_NAME_NULL", message = "Name mustn't be null."))
            }

            request.name.isEmpty() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_NAME_EMPTY", message = "Name mustn't be empty string."))
            }
        }
        when {
            request.mediaCount == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_MEDIA_COUNT_NULL", message = "Count of media mustn't be null."))
            }

            request.mediaCount <= 0 -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_MEDIA_COUNT_NOT_POSITIVE", message = "Count of media must be positive number."))
            }
        }
        if (request.format == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_FORMAT_NULL", message = "Format mustn't be null."))
        }
        if (request.crack == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_CRACK_NULL", message = "Crack mustn't be null."))
        }
        if (request.serialKey == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_SERIAL_KEY_NULL", message = "Serial key mustn't be null."))
        }
        if (request.patch == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_PATCH_NULL", message = "Patch mustn't be null."))
        }
        if (request.trainer == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_TRAINER_NULL", message = "Trainer mustn't be null."))
        }
        if (request.trainerData == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_TRAINER_DATA_NULL", message = "Data for trainer mustn't be null."))
        }
        if (request.editor == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_EDITOR_NULL", message = "Editor mustn't be null."))
        }
        if (request.saves == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "GAME_SAVES_NULL", message = "Saves mustn't be null."))
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
