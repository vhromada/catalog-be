package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Song
import com.github.vhromada.catalog.entity.io.ChangeSongRequest

/**
 * An interface represents mapper for songs.
 *
 * @author Vladimir Hromada
 */
interface SongMapper {

    /**
     * Maps song.
     *
     * @param source song
     * @return mapped song
     */
    fun mapSong(source: Song): com.github.vhromada.catalog.entity.Song

    /**
     * Maps list of songs.
     *
     * @param source list of songs
     * @return mapped list of songs
     */
    fun mapSongs(source: List<Song>): List<com.github.vhromada.catalog.entity.Song>

    /**
     * Maps request for changing song.
     *
     * @param source request for changing song
     * @return mapped song
     */
    fun mapRequest(source: ChangeSongRequest): Song

}
