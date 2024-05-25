package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Song
import com.github.vhromada.catalog.entity.io.ChangeSongRequest
import com.github.vhromada.catalog.mapper.SongMapper
import com.github.vhromada.catalog.provider.UuidProvider
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for songs.
 *
 * @author Vladimir Hromada
 */
@Component("songMapper")
class SongMapperImpl(

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : SongMapper {

    override fun mapSong(source: Song): com.github.vhromada.catalog.entity.Song {
        return com.github.vhromada.catalog.entity.Song(
            uuid = source.uuid,
            name = source.name,
            length = source.length,
            note = source.note
        )
    }

    override fun mapSongs(source: List<Song>): List<com.github.vhromada.catalog.entity.Song> {
        return source.map { mapSong(source = it) }
    }

    override fun mapRequest(source: ChangeSongRequest): Song {
        return Song(
            id = null,
            uuid = uuidProvider.getUuid(),
            name = source.name!!,
            length = source.length!!,
            note = source.note
        )
    }

}
