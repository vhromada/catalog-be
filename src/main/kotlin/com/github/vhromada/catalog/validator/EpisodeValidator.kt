package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangeEpisodeRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for episodes.
 *
 * @author Vladimir Hromada
 */
interface EpisodeValidator {

    /**
     * Validates request for changing episode.
     * <br></br>
     * Validation errors:
     *
     *  * Number of episode is null
     *  * Number of episode isn't positive number
     *  * Name is null
     *  * Name is empty string
     *  * Length of episode is null
     *  * Length of episode is negative value
     *
     * @param request request for changing episode
     * @throws InputException if request for changing episode isn't valid
     */
    fun validateRequest(request: ChangeEpisodeRequest)

}
