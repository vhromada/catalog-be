package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Song
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for songs.
 *
 * @author Vladimir Hromada
 */
interface SongService {

    /**
     * Returns page of songs by music's ID.
     *
     * @param music    music's ID
     * @param pageable paging information
     * @return page of songs by music's ID
     */
    fun search(music: Int, pageable: Pageable): Page<Song>

    /**
     * Returns song.
     *
     * @param uuid UUID
     * @return song
     * @throws InputException if song doesn't exist in data storage
     */
    fun get(uuid: String): Song

    /**
     * Stores song.
     *
     * @param song song
     * @return stored song
     */
    fun store(song: Song): Song

    /**
     * Removes song.
     *
     * @param song song
     */
    fun remove(song: Song)

    /**
     * Duplicates song.
     *
     * @param song song
     * @return duplicated song
     */
    fun duplicate(song: Song): Song

}
