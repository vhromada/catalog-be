package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Movie
import com.github.vhromada.catalog.entity.MovieStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeMovieRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for movies.
 *
 * @author Vladimir Hromada
 */
interface MovieFacade {

    /**
     * Returns page of movies for filter.
     *
     * @param filter filter
     * @return page of movies for filter
     */
    fun search(filter: MultipleNameFilter): Page<Movie>

    /**
     * Returns movie.
     *
     * @param uuid UUID
     * @return movie
     * @throws InputException if movie doesn't exist in data storage
     */
    fun get(uuid: String): Movie

    /**
     * Adds movie.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Year is null
     *  * Year isn't between 1930 and current year
     *  * Languages are null
     *  * Languages are empty
     *  * Languages contain null value
     *  * Subtitles are null
     *  * Subtitles contain null value
     *  * Media are null
     *  * Media contain null value
     *  * Medium is negative value
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *  * Language doesn't exist in data storage
     *  * Subtitles doesn't exist in data storage
     *  * Picture doesn't exist in data storage
     *  * Genre doesn't exist in data storage
     *
     * @param request request for changing movie
     * @return created movie
     * @throws InputException if request for changing movie isn't valid
     */
    fun add(request: ChangeMovieRequest): Movie

    /**
     * Updates movie.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Year is null
     *  * Year isn't between 1930 and current year
     *  * Languages are null
     *  * Languages are empty
     *  * Languages contain null value
     *  * Subtitles are null
     *  * Subtitles contain null value
     *  * Media are null
     *  * Media contain null value
     *  * Medium is negative value
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *  * Language doesn't exist in data storage
     *  * Subtitles doesn't exist in data storage
     *  * Picture doesn't exist in data storage
     *  * Genre doesn't exist in data storage
     *  * Movie doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing movie
     * @return updated movie
     * @throws InputException if request for changing movie isn't valid
     */
    fun update(uuid: String, request: ChangeMovieRequest): Movie

    /**
     * Removes movie.
     *
     * @param uuid UUID
     * @throws InputException if movie doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Duplicates data.
     *
     * @param uuid UUID
     * @return created duplicated movie
     * @throws InputException if movie doesn't exist in data storage
     */
    fun duplicate(uuid: String): Movie

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): MovieStatistics

}
