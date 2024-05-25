package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Song
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeSongRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.SongFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * A class represents controller for songs.
 *
 * @author Vladimir Hromada
 */
@RestController("songController")
@RequestMapping("rest/music/{musicUuid}/songs")
@Tag(name = "Music")
class SongController(

    /**
     * Facade for songs
     */
    private val facade: SongFacade

) {

    /**
     * Returns page of songs for specified music and filter.
     * <br></br>
     * Validation errors:
     *
     *  * Music doesn't exist in data storage
     *
     * @param musicUuid music's UUID
     * @param filter    filter
     * @return page of songs for specified music and filter
     */
    @GetMapping
    fun search(
        @PathVariable("musicUuid") musicUuid: String,
        filter: PagingFilter
    ): Page<Song> {
        return facade.findAll(music = musicUuid, filter = filter)
    }

    /**
     * Returns song.
     * <br></br>
     * Validation errors:
     *
     *  * Music doesn't exist in data storage
     *  * Song doesn't exist in data storage
     *
     * @param musicUuid music's UUID
     * @param songUuid  song's UUID
     * @return song
     */
    @GetMapping("{songUuid}")
    fun get(
        @PathVariable("musicUuid") musicUuid: String,
        @PathVariable("songUuid") songUuid: String
    ): Song {
        return facade.get(music = musicUuid, uuid = songUuid)
    }

    /**
     * Adds song.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Length of song is null
     *  * Length of song is negative value
     *  * Music doesn't exist in data storage
     *
     * @param musicUuid music's UUID
     * @param request   request fot changing song
     * @return added song
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(
        @PathVariable("musicUuid") musicUuid: String,
        @RequestBody request: ChangeSongRequest
    ): Song {
        return facade.add(music = musicUuid, request = request)
    }

    /**
     * Updates song.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Length of song is null
     *  * Length of song is negative value
     *  * Music doesn't exist in data storage
     *  * Song doesn't exist in data storage
     *
     * @param musicUuid music's UUID
     * @param songUuid  song's UUID
     * @param request   request fot changing song
     * @return updated song
     */
    @PutMapping("{songUuid}")
    fun update(
        @PathVariable("musicUuid") musicUuid: String,
        @PathVariable("songUuid") songUuid: String,
        @RequestBody request: ChangeSongRequest
    ): Song {
        return facade.update(music = musicUuid, uuid = songUuid, request = request)
    }

    /**
     * Removes song.
     * <br></br>
     * Validation errors:
     *
     *  * Music doesn't exist in data storage
     *  * Song doesn't exist in data storage
     *
     * @param musicUuid music's UUID
     * @param songUuid  song's UUID
     */
    @DeleteMapping("{songUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable("musicUuid") musicUuid: String,
        @PathVariable("songUuid") songUuid: String
    ) {
        facade.remove(music = musicUuid, uuid = songUuid)
    }

    /**
     * Duplicates song.
     * <br></br>
     * Validation errors:
     *
     *  * Music doesn't exist in data storage
     *  * Song doesn't exist in data storage
     *
     * @param musicUuid music's UUID
     * @param songUuid  song's UUID
     * @return duplicated song
     */
    @PostMapping("{songUuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(
        @PathVariable("musicUuid") musicUuid: String,
        @PathVariable("songUuid") songUuid: String
    ): Song {
        return facade.duplicate(music = musicUuid, uuid = songUuid)
    }

}
