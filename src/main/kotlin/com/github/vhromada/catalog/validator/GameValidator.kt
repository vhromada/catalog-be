package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeGameRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for games.
 *
 * @author Vladimir Hromada
 */
interface GameValidator {

    /**
     * Validates request for changing game.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *  * Format is null
     *  * Crack is null
     *  * Serial key is null
     *  * Patch is null
     *  * Trainer is null
     *  * Data for trainer are null
     *  * Editor is null
     *  * Saves are null
     *
     * @param request request for changing game
     * @throws InputException if request for changing game isn't valid
     */
    fun validateRequest(request: ChangeGameRequest)

}
