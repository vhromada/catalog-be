package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Music
import com.github.vhromada.catalog.entity.MusicStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeMusicRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.MusicFacade
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
 * A class represents controller for music.
 *
 * @author Vladimir Hromada
 */
@RestController("musicController")
@RequestMapping("rest/music")
@Tag(name = "Music")
class MusicController(

    /**
     * Facade for music
     */
    private val facade: MusicFacade

) {

    /**
     * Returns list of music for filter.
     *
     * @param filter filter
     * @return list of music for filter
     */
    @GetMapping
    fun search(filter: NameFilter): Page<Music> {
        return facade.search(filter = filter)
    }

    /**
     * Returns music.
     * <br></br>
     * Validation errors:
     *
     *  * Music doesn't exist in data storage
     *
     * @param uuid UUID
     * @return music
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable uuid: String): Music {
        return facade.get(uuid = uuid)
    }

    /**
     * Adds music.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *
     * @param request request for changing music
     * @return added music
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeMusicRequest): Music {
        return facade.add(request = request)
    }

    /**
     * Updates music.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *  * Music doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing music
     * @return updated music
     */
    @PutMapping("{uuid}")
    fun update(
        @PathVariable uuid: String,
        @RequestBody request: ChangeMusicRequest
    ): Music {
        return facade.update(uuid = uuid, request = request)
    }

    /**
     * Removes music.
     * <br></br>
     * Validation errors:
     *
     *  * Music doesn't exist in data storage
     *
     * @param uuid UUID
     */
    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable uuid: String) {
        facade.remove(uuid = uuid)
    }

    /**
     * Duplicates music.
     * <br></br>
     * Validation errors:
     *
     *  * Music doesn't exist in data storage
     *
     * @param uuid UUID
     * @return duplicated music
     */
    @PostMapping("{uuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable uuid: String): Music {
        return facade.duplicate(uuid = uuid)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): MusicStatistics {
        return facade.getStatistics()
    }

}
