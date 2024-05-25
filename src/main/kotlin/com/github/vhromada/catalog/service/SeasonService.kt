package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.domain.Season
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for seasons.
 *
 * @author Vladimir Hromada
 */
interface SeasonService {

    /**
     * Returns page of seasons by show's ID.
     *
     * @param show     show's ID
     * @param pageable paging information
     * @return page of seasons by show's ID
     */
    fun search(show: Int, pageable: Pageable): Page<Season>

    /**
     * Returns season.
     *
     * @param uuid UUID
     * @return season
     * @throws InputException if season doesn't exist in data storage
     */
    fun get(uuid: String): Season

    /**
     * Stores season.
     *
     * @param season season
     * @return stored season
     */
    fun store(season: Season): Season

    /**
     * Removes season.
     *
     * @param season season
     */
    fun remove(season: Season)

    /**
     * Duplicates season.
     *
     * @param season season
     * @return duplicated season
     */
    fun duplicate(season: Season): Season


}
