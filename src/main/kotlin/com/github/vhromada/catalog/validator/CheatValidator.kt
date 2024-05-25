package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeCheatRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for cheats.
 *
 * @author Vladimir Hromada
 */
interface CheatValidator {

    /**
     * Validates request for changing cheat.
     * <br></br>
     * Validation errors:
     *
     *  * Cheat's data are null
     *  * Cheat's data contain null value
     *  * Action is null
     *  * Action is empty string
     *  * Description is null
     *  * Description is empty string
     *
     * @param request request for changing cheat
     * @throws InputException if request for changing cheat isn't valid
     */
    fun validateRequest(request: ChangeCheatRequest)

}
