package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Music
import com.github.vhromada.catalog.domain.filter.MusicFilter
import com.github.vhromada.catalog.domain.io.MusicStatistics
import com.github.vhromada.catalog.domain.io.SongStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeMusicRequest

/**
 * An interface represents mapper for music.
 *
 * @author Vladimir Hromada
 */
interface MusicMapper {

    /**
     * Maps music.
     *
     * @param source music
     * @return mapped music
     */
    fun mapMusic(source: Music): com.github.vhromada.catalog.entity.Music

    /**
     * Maps list of music.
     *
     * @param source list of music
     * @return mapped list of music
     */
    fun mapMusicList(source: List<Music>): List<com.github.vhromada.catalog.entity.Music>

    /**
     * Maps request for changing music.
     *
     * @param source request for changing music
     * @return mapped music
     */
    fun mapRequest(source: ChangeMusicRequest): Music

    /**
     * Maps filter for music.
     *
     * @param source filter for name
     * @return mapped filter for music
     */
    fun mapFilter(source: NameFilter): MusicFilter

    /**
     * Maps statistics.
     *
     * @param musicStatistics statistics for music
     * @param songStatistics  statistics for songs
     * @return statistics for music
     */
    fun mapStatistics(musicStatistics: MusicStatistics, songStatistics: SongStatistics): com.github.vhromada.catalog.entity.MusicStatistics

}
