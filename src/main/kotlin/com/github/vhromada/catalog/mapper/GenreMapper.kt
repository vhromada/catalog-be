package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Genre
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest

/**
 * An interface represents mapper for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreMapper {

    /**
     * Maps genre.
     *
     * @param source genre
     * @return mapped genre
     */
    fun mapGenre(source: Genre): com.github.vhromada.catalog.entity.Genre

    /**
     * Maps list of genres.
     *
     * @param source list of genres
     * @return mapped list of genres
     */
    fun mapGenres(source: List<Genre>): List<com.github.vhromada.catalog.entity.Genre>

    /**
     * Maps request for changing genre.
     *
     * @param source request for changing genre
     * @return mapped genre
     */
    fun mapRequest(source: ChangeGenreRequest): Genre

    /**
     * Maps filter for genres.
     *
     * @param source filter for name
     * @return mapped filter for genres
     */
    fun mapFilter(source: NameFilter): GenreFilter

}
