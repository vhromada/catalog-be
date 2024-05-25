package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Movie
import com.github.vhromada.catalog.domain.filter.MovieFilter
import com.github.vhromada.catalog.domain.io.MediaStatistics
import com.github.vhromada.catalog.entity.MovieStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeMovieRequest

/**
 * An interface represents mapper for movies.
 *
 * @author Vladimir Hromada
 */
interface MovieMapper {

    /**
     * Maps movie.
     *
     * @param source movie
     * @return mapped movie
     */
    fun mapMovie(source: Movie): com.github.vhromada.catalog.entity.Movie

    /**
     * Maps list of movies.
     *
     * @param source list of movies
     * @return mapped list of movies
     */
    fun mapMovies(source: List<Movie>): List<com.github.vhromada.catalog.entity.Movie>

    /**
     * Maps request for changing movie.
     *
     * @param source request for changing movie
     * @return mapped movie
     */
    fun mapRequest(source: ChangeMovieRequest): Movie

    /**
     * Maps filter for movies.
     *
     * @param source filter for multiple names
     * @return mapped filter for movies
     */
    fun mapFilter(source: MultipleNameFilter): MovieFilter

    /**
     * Maps statistics.
     *
     * @param count           count of movies
     * @param mediaStatistics statistics for media
     * @return statistics for movies
     */
    fun mapStatistics(count: Long, mediaStatistics: MediaStatistics): MovieStatistics

}
