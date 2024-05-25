package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Show
import com.github.vhromada.catalog.entity.ShowStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeShowRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for shows.
 *
 * @author Vladimir Hromada
 */
interface ShowFacade {

    /**
     * Returns page of shows for filter.
     *
     * @param filter filter
     * @return page of shows for filter
     */
    fun search(filter: MultipleNameFilter): Page<Show>

    /**
     * Returns show.
     *
     * @param uuid UUID
     * @return show
     * @throws InputException if show doesn't exist in data storage
     */
    fun get(uuid: String): Show

    /**
     * Adds show.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *  * Picture doesn't exist in data storage
     *  * Genre doesn't exist in data storage
     *
     * @param request request for changing show
     * @return created show
     * @throws InputException if request for changing show isn't valid
     */
    fun add(request: ChangeShowRequest): Show

    /**
     * Updates show.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *  * Picture doesn't exist in data storage
     *  * Genre doesn't exist in data storage
     *  * Show doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing show
     * @return updated show
     * @throws InputException if request for changing show isn't valid
     */
    fun update(uuid: String, request: ChangeShowRequest): Show

    /**
     * Removes show.
     *
     * @param uuid UUID
     * @throws InputException if show doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Duplicates data.
     *
     * @param uuid UUID
     * @return created duplicated show
     * @throws InputException if show doesn't exist in data storage
     */
    fun duplicate(uuid: String): Show

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): ShowStatistics

}
