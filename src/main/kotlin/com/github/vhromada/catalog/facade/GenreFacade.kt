package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Genre
import com.github.vhromada.catalog.entity.GenreStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreFacade {

    /**
     * Returns page of genres for filter.
     *
     * @param filter filter
     * @return page of genres for filter
     */
    fun search(filter: NameFilter): Page<Genre>

    /**
     * Returns genre.
     *
     * @param uuid UUID
     * @return genre
     * @throws InputException if genre doesn't exist in data storage
     */
    fun get(uuid: String): Genre

    /**
     * Adds genre.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *
     * @param request request for changing genre
     * @return created genre
     * @throws InputException if request for changing genre isn't valid
     */
    fun add(request: ChangeGenreRequest): Genre

    /**
     * Updates genre.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Genre doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing genre
     * @return updated genre
     * @throws InputException if request for changing genre isn't valid
     */
    fun update(uuid: String, request: ChangeGenreRequest): Genre

    /**
     * Removes genre.
     *
     * @param uuid UUID
     * @throws InputException if genre doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Duplicates data.
     *
     * @param uuid UUID
     * @return created duplicated genre
     * @throws InputException if genre doesn't exist in data storage
     */
    fun duplicate(uuid: String): Genre

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): GenreStatistics

}
