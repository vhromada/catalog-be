package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.filter.NameFilter
import java.time.LocalDateTime

/**
 * A class represents constants for tests.
 *
 * @author Vladimir Hromada
 */
object TestConstants {

    /**
     * UUID
     */
    const val UUID = "7218e3f3-9bf9-48c5-a44d-31ba36b726f7"

    /**
     * Username
     */
    const val USERNAME = "catalog"

    /**
     * Password
     */
    const val PASSWORD = "catalog"

    /**
     * Time
     */
    val TIME: LocalDateTime = LocalDateTime.of(2000, 2, 4, 10, 45, 55, 70)

    /**
     * Filter for name
     */
    val NAME_FILTER = NameFilter(name = "name")

    /**
     * Filter for multiple names
     */
    val MULTIPLE_NAMES_FILTER = MultipleNameFilter(czechName = "czech", originalName = "original")

    /**
     * Bad minimum IMDB code
     */
    const val BAD_MIN_IMDB_CODE = 0

    /**
     * Bad maximum IMDB code
     */
    const val BAD_MAX_IMDB_CODE = Constants.MAX_IMDB_CODE + 1

    /**
     * Bad minimal year
     */
    const val BAD_MIN_YEAR = Constants.MIN_YEAR - 1

    /**
     * Bad maximal year
     */
    val BAD_MAX_YEAR = Constants.CURRENT_YEAR + 1

    /**
     * Event for invalid movie year
     */
    val INVALID_MOVIE_YEAR_EVENT = Event(severity = Severity.ERROR, key = "MOVIE_YEAR_NOT_VALID", message = "Year must be between ${Constants.MIN_YEAR} and ${Constants.CURRENT_YEAR}.")

    /**
     * Event for invalid movie IMDB code
     */
    val INVALID_MOVIE_IMDB_CODE_EVENT = Event(severity = Severity.ERROR, key = "MOVIE_IMDB_CODE_NOT_VALID", message = "IMDB code must be between 1 and 999999999.")

    /**
     * Event for invalid movie IMDB code
     */
    val INVALID_SHOW_IMDB_CODE_EVENT = Event(severity = Severity.ERROR, key = "SHOW_IMDB_CODE_NOT_VALID", message = "IMDB code must be between 1 and 999999999.")

    /**
     * Event for invalid starting year
     */
    val INVALID_STARTING_YEAR_EVENT = Event(
        severity = Severity.ERROR,
        key = "SEASON_START_YEAR_NOT_VALID",
        message = "Starting year must be between ${Constants.MIN_YEAR} and ${Constants.CURRENT_YEAR}."
    )

    /**
     * Event for invalid ending year
     */
    val INVALID_ENDING_YEAR_EVENT = Event(
        severity = Severity.ERROR,
        key = "SEASON_END_YEAR_NOT_VALID",
        message = "Ending year must be between ${Constants.MIN_YEAR} and ${Constants.CURRENT_YEAR}."
    )

}
