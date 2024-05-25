package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Joke
import com.github.vhromada.catalog.entity.JokeStatistics
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeJokeRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for jokes.
 *
 * @author Vladimir Hromada
 */
interface JokeFacade {

    /**
     * Returns page of jokes for filter.
     *
     * @param filter filter
     * @return page of jokes for filter
     */
    fun search(filter: PagingFilter): Page<Joke>

    /**
     * Returns joke.
     *
     * @param uuid UUID
     * @return joke
     * @throws InputException if joke doesn't exist in data storage
     */
    fun get(uuid: String): Joke

    /**
     * Adds joke.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *
     * @param request request for changing joke
     * @return created joke
     * @throws InputException if request for changing joke isn't valid
     */
    fun add(request: ChangeJokeRequest): Joke

    /**
     * Updates joke.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Joke doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing joke
     * @return updated joke
     * @throws InputException if request for changing joke isn't valid
     */
    fun update(uuid: String, request: ChangeJokeRequest): Joke

    /**
     * Removes joke.
     *
     * @param uuid UUID
     * @throws InputException if joke doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): JokeStatistics

}
