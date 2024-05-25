package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Episode
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for episodes.
 *
 * @author Vladimir Hromada
 */
interface EpisodeService {

    /**
     * Returns page of episodes by season's ID.
     *
     * @param season   season's ID
     * @param pageable paging information
     * @return page of episodes by season's ID
     */
    fun search(season: Int, pageable: Pageable): Page<Episode>

    /**
     * Returns episode.
     *
     * @param uuid UUID
     * @return episode
     * @throws InputException if episode doesn't exist in data storage
     */
    fun get(uuid: String): Episode

    /**
     * Stores episode.
     *
     * @param episode episode
     * @return stored episode
     */
    fun store(episode: Episode): Episode

    /**
     * Removes episode.
     *
     * @param episode episode
     */
    fun remove(episode: Episode)

    /**
     * Duplicates episode.
     *
     * @param episode episode
     * @return duplicated episode
     */
    fun duplicate(episode: Episode): Episode

}
