package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Song
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeSongRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for songs.
 *
 * @author Vladimir Hromada
 */
interface SongFacade {

    /**
     * Returns page of songs by music's UUID and filter.
     *
     * @param music  music's UUID
     * @param filter filter
     * @return page of songs by music's UUID and filter
     * @throws InputException if music doesn't exist in data storage
     */
    fun findAll(music: String, filter: PagingFilter): Page<Song>

    /**
     * Returns song.
     *
     * @param music music's UUID
     * @param uuid  song's UUID
     * @return song
     * @throws InputException if music doesn't exist in data storage
     * or song doesn't exist in data storage
     */
    fun get(music: String, uuid: String): Song

    /**
     * Adds song.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Length of song is null
     *  * Length of song is negative value
     *
     * @param music   music's UUID
     * @param request request for changing song
     * @return created song
     * @throws InputException if music doesn't exist in data storage
     * or request for changing song isn't valid
     */
    fun add(music: String, request: ChangeSongRequest): Song

    /**
     * Updates song.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Length of song is null
     *  * Length of song is negative value
     *  * Song doesn't exist in data storage
     *
     * @param music   music's UUID
     * @param uuid    song's UUID
     * @param request request for changing song
     * @return updated song
     * @throws InputException if music doesn't exist in data storage
     * or request for changing song isn't valid
     */
    fun update(music: String, uuid: String, request: ChangeSongRequest): Song

    /**
     * Removes song.
     *
     * @param music music's UUID
     * @param uuid  song's UUID
     * @throws InputException if music doesn't exist in data storage
     * or song doesn't exist in data storage
     */
    fun remove(music: String, uuid: String)

    /**
     * Duplicates data.
     *
     * @param music music's UUID
     * @param uuid  song's UUID
     * @return created duplicated song
     * @throws InputException if music doesn't exist in data storage
     * or song doesn't exist in data storage
     */
    fun duplicate(music: String, uuid: String): Song

}
