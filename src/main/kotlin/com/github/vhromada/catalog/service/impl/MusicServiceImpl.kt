package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Music
import com.github.vhromada.catalog.domain.filter.MusicFilter
import com.github.vhromada.catalog.entity.MusicStatistics
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.MusicMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.MusicRepository
import com.github.vhromada.catalog.repository.SongRepository
import com.github.vhromada.catalog.service.MusicService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for music.
 *
 * @author Vladimir Hromada
 */
@Service("musicService")
class MusicServiceImpl(

    /**
     * Repository for music
     */
    private val musicRepository: MusicRepository,

    /**
     * Repository for songs
     */
    private val songRepository: SongRepository,

    /**
     * Mapper for music
     */
    private val mapper: MusicMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : MusicService {

    override fun search(filter: MusicFilter, pageable: Pageable): Page<Music> {
        if (filter.isEmpty()) {
            return musicRepository.findAll(pageable)
        }
        return musicRepository.findAll(filter.toSpecification(), pageable)
    }

    override fun get(uuid: String): Music {
        return musicRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "MUSIC_NOT_EXIST", message = "Music doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(music: Music): Music {
        return musicRepository.save(music)
    }

    @Transactional
    override fun remove(music: Music) {
        musicRepository.delete(music)
    }

    @Transactional
    override fun duplicate(music: Music): Music {
        val copy = music.copy(id = null, uuid = uuidProvider.getUuid(), songs = mutableListOf())
        copy.songs.addAll(music.songs.map { it.copy(id = null, uuid = uuidProvider.getUuid(), music = copy) })
        return musicRepository.save(copy)
    }

    override fun getStatistics(): MusicStatistics {
        val musicStatistics = musicRepository.getStatistics()
        val songStatistics = songRepository.getStatistics()
        return mapper.mapStatistics(musicStatistics = musicStatistics, songStatistics = songStatistics)
    }

}
