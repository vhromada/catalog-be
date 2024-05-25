package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Episode
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeEpisodeRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for episodes.
 *
 * @author Vladimir Hromada
 */
interface EpisodeFacade {

    /**
     * Returns page of episodes by season's UUID and filter.
     *
     * @param show   show's UUID
     * @param season season's UUID
     * @param filter filter
     * @return page of episodes by season's UUID and filter
     * @throws InputException if show doesn't exist in data storage
     * or season doesn't exist in data storage
     */
    fun findAll(show: String, season: String, filter: PagingFilter): Page<Episode>

    /**
     * Returns episode.
     *
     * @param show   show's UUID
     * @param season season's UUID
     * @param uuid   episode's UUID
     * @return episode
     * @throws InputException if show doesn't exist in data storage
     * or season doesn't exist in data storage
     * or episode doesn't exist in data storage
     */
    fun get(show: String, season: String, uuid: String): Episode

    /**
     * Adds episode.
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
     * @param show    show's UUID
     * @param season  season's UUID
     * @param request request for changing episode
     * @return created episode
     * @throws InputException if show doesn't exist in data storage
     * or season doesn't exist in data storage
     * or request for changing episode isn't valid
     */
    fun add(show: String, season: String, request: ChangeEpisodeRequest): Episode

    /**
     * Updates episode.
     * <br></br>
     * Validation errors:
     *
     *  * Number of episode is null
     *  * Number of episode isn't positive number
     *  * Name is null
     *  * Name is empty string
     *  * Length of episode is null
     *  * Length of episode is negative value
     *  * Episode doesn't exist in data storage
     *
     * @param show    show's UUID
     * @param season  season's UUID
     * @param uuid    episode's UUID
     * @param request request for changing episode
     * @return updated episode
     * @throws InputException if show doesn't exist in data storage
     * or season doesn't exist in data storage
     * or request for changing episode isn't valid
     */
    fun update(show: String, season: String, uuid: String, request: ChangeEpisodeRequest): Episode

    /**
     * Removes episode.
     *
     * @param show   show's UUID
     * @param season season's UUID
     * @param uuid   episode's UUID
     * @throws InputException if show doesn't exist in data storage
     * or season doesn't exist in data storage
     * or episode doesn't exist in data storage
     */
    fun remove(show: String, season: String, uuid: String)

    /**
     * Duplicates data.
     *
     * @param show   show's UUID
     * @param season season's UUID
     * @param uuid   episode's UUID
     * @return created duplicated episode
     * @throws InputException if show doesn't exist in data storage
     * or season doesn't exist in data storage
     * or episode doesn't exist in data storage
     */
    fun duplicate(show: String, season: String, uuid: String): Episode

}
