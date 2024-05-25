package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Season
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeSeasonRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for seasons.
 *
 * @author Vladimir Hromada
 */
interface SeasonFacade {

    /**
     * Returns page of seasons by show's UUID and filter.
     *
     * @param show   show's UUID
     * @param filter filter
     * @return page of seasons by show's UUID and filter
     * @throws InputException if show doesn't exist in data storage
     */
    fun findAll(show: String, filter: PagingFilter): Page<Season>

    /**
     * Returns season.
     *
     * @param show show's UUID
     * @param uuid season's UUID
     * @return season
     * @throws InputException if show doesn't exist in data storage
     * or season doesn't exist in data storage
     */
    fun get(show: String, uuid: String): Season

    /**
     * Adds season.
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
     *  * Language doesn't exist in data storage
     *  * Subtitles doesn't exist in data storage
     *
     * @param show    show's UUID
     * @param request request for changing season
     * @return created season
     * @throws InputException if show doesn't exist in data storage
     * or request for changing season isn't valid
     */
    fun add(show: String, request: ChangeSeasonRequest): Season

    /**
     * Updates season.
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
     *  * Language doesn't exist in data storage
     *  * Subtitles doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *
     * @param show    show's UUID
     * @param uuid    season's UUID
     * @param request request for changing season
     * @return updated season
     * @throws InputException if show doesn't exist in data storage
     * or request for changing season isn't valid
     */
    fun update(show: String, uuid: String, request: ChangeSeasonRequest): Season

    /**
     * Removes season.
     *
     * @param show show's UUID
     * @param uuid season's UUID
     * @throws InputException if show doesn't exist in data storage
     * or season doesn't exist in data storage
     */
    fun remove(show: String, uuid: String)

    /**
     * Duplicates data.
     *
     * @param show show's UUID
     * @param uuid season's UUID
     * @return created duplicated season
     * @throws InputException if show doesn't exist in data storage
     * or season doesn't exist in data storage
     */
    fun duplicate(show: String, uuid: String): Season

}
