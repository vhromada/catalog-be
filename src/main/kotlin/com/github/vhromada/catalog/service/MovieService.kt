package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Movie
import com.github.vhromada.catalog.domain.filter.MovieFilter
import com.github.vhromada.catalog.entity.MovieStatistics
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for movies.
 *
 * @author Vladimir Hromada
 */
interface MovieService {

    /**
     * Returns page of movies by filter.
     *
     * @param filter   filter
     * @param pageable paging information
     * @return page of movies by filter
     */
    fun search(filter: MovieFilter, pageable: Pageable): Page<Movie>

    /**
     * Returns movie.
     *
     * @param uuid UUID
     * @return movie
     * @throws InputException if movie doesn't exist in data storage
     */
    fun get(uuid: String): Movie

    /**
     * Stores movie.
     *
     * @param movie movie
     * @return stored movie
     */
    fun store(movie: Movie): Movie

    /**
     * Removes movie.
     *
     * @param movie movie
     */
    fun remove(movie: Movie)

    /**
     * Duplicates movie.
     *
     * @param movie movie
     * @return duplicated movie
     */
    fun duplicate(movie: Movie): Movie

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): MovieStatistics

}
