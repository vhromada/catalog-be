package com.github.vhromada.catalog.validator.impl

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.io.ChangeSeasonRequest
import com.github.vhromada.catalog.utils.Constants
import com.github.vhromada.catalog.validator.SeasonValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of validator for seasons.
 *
 * @author Vladimir Hromada
 */
@Component("seasonValidator")
class SeasonValidatorImpl : SeasonValidator {

    override fun validateRequest(request: ChangeSeasonRequest) {
        val result = Result<Unit>()
        when {
            request.number == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SEASON_NUMBER_NULL", message = "Number of season mustn't be null."))
            }

            request.number <= 0 -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SEASON_NUMBER_NOT_POSITIVE", message = "Number of season must be positive number."))
            }
        }
        validateYears(request = request, result = result)
        validateLanguages(request = request, result = result)
        if (result.isError()) {
            throw InputException(result = result)
        }
    }

    /**
     * Validates years.
     * <br></br>
     * Validation errors:
     *
     *  * Starting year is null
     *  * Starting year isn't between 1930 and current year
     *  * Ending year is null
     *  * Ending year isn't between 1930 and current year
     *  * Starting year is greater than ending year
     *
     * @param request request for changing season
     * @param result  result with validation errors
     */
    private fun validateYears(request: ChangeSeasonRequest, result: Result<Unit>) {
        when {
            request.startYear == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SEASON_START_YEAR_NULL", message = "Starting year mustn't be null."))
            }

            request.startYear < Constants.MIN_YEAR || request.startYear > Constants.CURRENT_YEAR -> {
                result.addEvent(
                    event = Event(
                        severity = Severity.ERROR,
                        key = "SEASON_START_YEAR_NOT_VALID",
                        message = "Starting year must be between ${Constants.MIN_YEAR} and ${Constants.CURRENT_YEAR}."
                    )
                )
            }
        }
        when {
            request.endYear == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SEASON_END_YEAR_NULL", message = "Ending year mustn't be null."))
            }

            request.endYear < Constants.MIN_YEAR || request.endYear > Constants.CURRENT_YEAR -> {
                result.addEvent(
                    event = Event(
                        severity = Severity.ERROR,
                        key = "SEASON_END_YEAR_NOT_VALID",
                        message = "Ending year must be between ${Constants.MIN_YEAR} and ${Constants.CURRENT_YEAR}."
                    )
                )
            }
        }
        if (request.startYear != null && request.endYear != null && request.startYear > request.endYear) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "SEASON_YEARS_NOT_VALID", message = "Starting year mustn't be greater than ending year."))
        }
    }

    /**
     * Validates languages.
     * <br></br>
     * Validation errors:
     *
     *  * Language is null
     *  * Subtitles are null
     *  * Subtitles contain null value
     *
     * @param request request for changing season
     * @param result  result with validation errors
     */
    private fun validateLanguages(request: ChangeSeasonRequest, result: Result<Unit>) {
        if (request.language == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "SEASON_LANGUAGE_NULL", message = "Language mustn't be null."))
        }
        when {
            request.subtitles == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SEASON_SUBTITLES_NULL", message = "Subtitles mustn't be null."))
            }

            request.subtitles.contains(null) -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "SEASON_SUBTITLES_CONTAIN_NULL", message = "Subtitles mustn't contain null value."))
            }
        }
    }

}
