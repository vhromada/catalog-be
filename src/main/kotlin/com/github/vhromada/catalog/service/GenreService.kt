package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Genre
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreService {

    /**
     * Returns page of genres by filter.
     *
     * @param filter   filter
     * @param pageable paging information
     * @return page of genres by filter
     */
    fun search(filter: GenreFilter, pageable: Pageable): Page<Genre>

    /**
     * Returns genre.
     *
     * @param uuid UUID
     * @return genre
     * @throws InputException if genre doesn't exist in data storage
     */
    fun get(uuid: String): Genre

    /**
     * Stores genre.
     *
     * @param genre genre
     * @return stored genre
     */
    fun store(genre: Genre): Genre

    /**
     * Removes genre.
     *
     * @param genre genre
     */
    fun remove(genre: Genre)

    /**
     * Duplicates genre.
     *
     * @param genre genre
     * @return duplicated genre
     */
    fun duplicate(genre: Genre): Genre

    /**
     * Returns count of genres.
     *
     * @return count of genres
     */
    fun getCount(): Long

}
