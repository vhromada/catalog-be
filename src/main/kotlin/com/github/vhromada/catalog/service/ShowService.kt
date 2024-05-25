package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Show
import com.github.vhromada.catalog.domain.filter.ShowFilter
import com.github.vhromada.catalog.entity.ShowStatistics
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for shows.
 *
 * @author Vladimir Hromada
 */
interface ShowService {

    /**
     * Returns page of shows by filter.
     *
     * @param filter   filter
     * @param pageable paging information
     * @return page of shows by filter
     */
    fun search(filter: ShowFilter, pageable: Pageable): Page<Show>

    /**
     * Returns show.
     *
     * @param uuid UUID
     * @return show
     * @throws InputException if show doesn't exist in data storage
     */
    fun get(uuid: String): Show

    /**
     * Stores show.
     *
     * @param show show
     * @return stored show
     */
    fun store(show: Show): Show

    /**
     * Removes show.
     *
     * @param show show
     */
    fun remove(show: Show)

    /**
     * Duplicates show.
     *
     * @param show show
     * @return duplicated show
     */
    fun duplicate(show: Show): Show

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): ShowStatistics

}
