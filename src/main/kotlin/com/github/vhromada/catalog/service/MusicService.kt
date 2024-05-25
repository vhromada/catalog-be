package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Music
import com.github.vhromada.catalog.domain.filter.MusicFilter
import com.github.vhromada.catalog.entity.MusicStatistics
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for music.
 *
 * @author Vladimir Hromada
 */
interface MusicService {

    /**
     * Returns page of music by filter.
     *
     * @param filter   filter
     * @param pageable paging information
     * @return page of music by filter
     */
    fun search(filter: MusicFilter, pageable: Pageable): Page<Music>

    /**
     * Returns music.
     *
     * @param uuid UUID
     * @return music
     * @throws InputException if music doesn't exist in data storage
     */
    fun get(uuid: String): Music

    /**
     * Stores music.
     *
     * @param music music
     * @return stored music
     */
    fun store(music: Music): Music

    /**
     * Removes music.
     *
     * @param music music
     */
    fun remove(music: Music)

    /**
     * Duplicates music.
     *
     * @param music music
     * @return duplicated music
     */
    fun duplicate(music: Music): Music

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): MusicStatistics

}
