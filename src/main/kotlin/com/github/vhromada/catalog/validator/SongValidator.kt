package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeSongRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for songs.
 *
 * @author Vladimir Hromada
 */
interface SongValidator {

    /**
     * Validates request for changing song.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Length of song is null
     *  * Length of song is negative value
     *
     * @param request request for changing song
     * @throws InputException if request for changing song isn't valid
     */
    fun validateRequest(request: ChangeSongRequest)

}
