package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Music
import com.github.vhromada.catalog.entity.MusicStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeMusicRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for music.
 *
 * @author Vladimir Hromada
 */
interface MusicFacade {

    /**
     * Returns page of music for filter.
     *
     * @param filter filter
     * @return page of music for filter
     */
    fun search(filter: NameFilter): Page<Music>

    /**
     * Returns music.
     *
     * @param uuid UUID
     * @return music
     * @throws InputException if music doesn't exist in data storage
     */
    fun get(uuid: String): Music

    /**
     * Adds music.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *
     * @param request request for changing music
     * @return created music
     * @throws InputException if request for changing music isn't valid
     */
    fun add(request: ChangeMusicRequest): Music

    /**
     * Updates music.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *  * Music doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing music
     * @return updated music
     * @throws InputException if request for changing music isn't valid
     */
    fun update(uuid: String, request: ChangeMusicRequest): Music

    /**
     * Removes music.
     *
     * @param uuid UUID
     * @throws InputException if music doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Duplicates data.
     *
     * @param uuid UUID
     * @return created duplicated music
     * @throws InputException if music doesn't exist in data storage
     */
    fun duplicate(uuid: String): Music

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): MusicStatistics

}
