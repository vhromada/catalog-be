package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Song
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.MusicRepository
import com.github.vhromada.catalog.repository.SongRepository
import com.github.vhromada.catalog.service.SongService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for songs.
 *
 * @author Vladimir Hromada
 */
@Service("songService")
class SongServiceImpl(

    /**
     * Repository for songs
     */
    private val songRepository: SongRepository,

    /**
     * Repository for music
     */
    private val musicRepository: MusicRepository,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : SongService {

    override fun search(music: Int, pageable: Pageable): Page<Song> {
        return songRepository.findAllByMusicId(id = music, pageable = pageable)
    }

    override fun get(uuid: String): Song {
        return songRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "SONG_NOT_EXIST", message = "Song doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(song: Song): Song {
        return songRepository.save(song)
    }

    @Transactional
    override fun remove(song: Song) {
        val music = song.music!!
        music.songs.remove(song)
        musicRepository.save(music)
    }

    @Transactional
    override fun duplicate(song: Song): Song {
        val copy = song.copy(id = null, uuid = uuidProvider.getUuid())
        copy.music!!.songs.add(copy)
        return songRepository.save(copy)
    }

}
