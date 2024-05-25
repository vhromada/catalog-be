package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Song
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeSongRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.SongFacade
import com.github.vhromada.catalog.mapper.SongMapper
import com.github.vhromada.catalog.service.MusicService
import com.github.vhromada.catalog.service.SongService
import com.github.vhromada.catalog.validator.SongValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for songs.
 *
 * @author Vladimir Hromada
 */
@Component("songFacade")
class SongFacadeImpl(

    /**
     * Service for songs
     */
    private val songService: SongService,

    /**
     * Service for music
     */
    private val musicService: MusicService,

    /**
     * Mapper for songs
     */
    private val mapper: SongMapper,

    /**
     * Validator for songs
     */
    private val validator: SongValidator

) : SongFacade {

    override fun findAll(music: String, filter: PagingFilter): Page<Song> {
        val songs = songService.search(music = musicService.get(uuid = music).id!!, pageable = filter.toPageable(sort = Sort.by("name", "id")))
        return Page(data = mapper.mapSongs(songs.content), page = songs)
    }

    override fun get(music: String, uuid: String): Song {
        musicService.get(uuid = music)
        return mapper.mapSong(source = songService.get(uuid = uuid))
    }

    override fun add(music: String, request: ChangeSongRequest): Song {
        val domainMusic = musicService.get(uuid = music)
        validator.validateRequest(request = request)
        return mapper.mapSong(source = songService.store(song = mapper.mapRequest(source = request).copy(music = domainMusic)))
    }

    override fun update(music: String, uuid: String, request: ChangeSongRequest): Song {
        musicService.get(uuid = music)
        validator.validateRequest(request = request)
        val song = songService.get(uuid = uuid)
        song.merge(song = mapper.mapRequest(source = request))
        return mapper.mapSong(source = songService.store(song = song))
    }

    override fun remove(music: String, uuid: String) {
        musicService.get(uuid = music)
        songService.remove(song = songService.get(uuid = uuid))
    }

    override fun duplicate(music: String, uuid: String): Song {
        musicService.get(uuid = music)
        return mapper.mapSong(source = songService.duplicate(song = songService.get(uuid = uuid)))
    }

}
