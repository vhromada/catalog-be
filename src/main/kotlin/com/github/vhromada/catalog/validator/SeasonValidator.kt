package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.entity.io.ChangeSeasonRequest

/**
 * An interface represents validator for seasons.
 *
 * @author Vladimir Hromada
 */
interface SeasonValidator {

    /**
     * Validates request for changing season.
     * <br></br>
     * Validation errors:
     *
     *  * Number of season is null
     *  * Number of season isn't positive number
     *  * Starting year is null
     *  * Starting year isn't between 1930 and current year
     *  * Ending year is null
     *  * Ending year isn't between 1930 and current year
     *  * Starting year is greater than ending year
     *  * Language is null
     *  * Subtitles are null
     *  * Subtitles contain null value
     *
     * @param request request for changing season
     * @throws InputException if request for changing season isn't valid
     */
    fun validateRequest(request: ChangeSeasonRequest)

}
