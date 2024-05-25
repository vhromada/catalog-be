package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeCheatRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.validator.CheatValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for cheats.
 *
 * @author Vladimir Hromada
 */
@Component("cheatValidator")
class CheatValidatorImpl : CheatValidator {

    override fun validateRequest(request: ChangeCheatRequest) {
        val result = Result<Unit>()
        if (request.data == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "CHEAT_DATA_NULL", message = "Cheat's data mustn't be null."))
        } else {
            if (request.data.contains(null)) {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "CHEAT_DATA_CONTAIN_NULL", message = "Cheat's data mustn't contain null value."))
            }
            for (cheatData in request.data) {
                if (cheatData != null) {
                    when {
                        cheatData.action == null -> {
                            result.addEvent(event = Event(severity = Severity.ERROR, key = "CHEAT_DATA_ACTION_NULL", message = "Cheat's data action mustn't be null."))
                        }

                        cheatData.action.isBlank() -> {
                            result.addEvent(event = Event(severity = Severity.ERROR, key = "CHEAT_DATA_ACTION_EMPTY", message = "Cheat's data action mustn't be empty string."))
                        }
                    }
                    when {
                        cheatData.description == null -> {
                            result.addEvent(event = Event(severity = Severity.ERROR, key = "CHEAT_DATA_DESCRIPTION_NULL", message = "Cheat's data description mustn't be null."))
                        }

                        cheatData.description.isBlank() -> {
                            result.addEvent(event = Event(severity = Severity.ERROR, key = "CHEAT_DATA_DESCRIPTION_EMPTY", message = "Cheat's data description mustn't be empty string."))
                        }
                    }
                }
            }
        }
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

}
