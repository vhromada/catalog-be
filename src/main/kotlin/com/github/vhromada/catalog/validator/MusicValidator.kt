package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeMusicRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for music.
 *
 * @author Vladimir Hromada
 */
interface MusicValidator {

    /**
     * Validates request for changing music.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *
     * @param request request for changing music
     * @throws InputException if request for changing music isn't valid
     */
    fun validateRequest(request: ChangeMusicRequest)

}
