package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.common.Time
import com.github.vhromada.catalog.domain.Music
import com.github.vhromada.catalog.domain.filter.MusicFilter
import com.github.vhromada.catalog.domain.io.MusicStatistics
import com.github.vhromada.catalog.domain.io.SongStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeMusicRequest
import com.github.vhromada.catalog.mapper.MusicMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for music.
 *
 * @author Vladimir Hromada
 */
@Component("musicMapper")
class MusicMapperImpl(

    /**
     * Service for normalizing strings
     */
    private val normalizerService: NormalizerService,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : MusicMapper {

    override fun mapMusic(source: Music): com.github.vhromada.catalog.entity.Music {
        return com.github.vhromada.catalog.entity.Music(
            uuid = source.uuid,
            name = source.name,
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            mediaCount = source.mediaCount,
            note = source.note,
            songsCount = source.songs.size,
            length = source.songs.sumOf { it.length }
        )
    }

    override fun mapMusicList(source: List<Music>): List<com.github.vhromada.catalog.entity.Music> {
        return source.map { mapMusic(source = it) }
    }

    override fun mapRequest(source: ChangeMusicRequest): Music {
        return Music(
            id = null,
            uuid = uuidProvider.getUuid(),
            name = source.name!!,
            normalizedName = normalizerService.normalize(source = source.name),
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            mediaCount = source.mediaCount!!,
            note = source.note,
            songs = mutableListOf()
        )
    }

    override fun mapFilter(source: NameFilter): MusicFilter {
        return MusicFilter(name = source.name)
    }

    override fun mapStatistics(musicStatistics: MusicStatistics, songStatistics: SongStatistics): com.github.vhromada.catalog.entity.MusicStatistics {
        return com.github.vhromada.catalog.entity.MusicStatistics(
            count = musicStatistics.count.toInt(),
            songsCount = songStatistics.count.toInt(),
            mediaCount = if (musicStatistics.mediaCount == null) 0 else musicStatistics.mediaCount.toInt(),
            length = Time(length = if (songStatistics.length == null) 0 else songStatistics.length.toInt()).toString()
        )
    }

}
