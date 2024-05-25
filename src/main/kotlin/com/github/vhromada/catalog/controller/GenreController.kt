package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Genre
import com.github.vhromada.catalog.entity.GenreStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.GenreFacade
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
 * A class represents controller for genres.
 *
 * @author Vladimir Hromada
 */
@RestController("genreController")
@RequestMapping("rest/genres")
@Tag(name = "Genres")
class GenreController(

    /**
     * Facade for genres
     */
    private val facade: GenreFacade

) {

    /**
     * Returns page of genres for filter.
     *
     * @param filter filter
     * @return page of genres for filter
     */
    @GetMapping
    fun search(filter: NameFilter): Page<Genre> {
        return facade.search(filter = filter)
    }

    /**
     * Returns genre.
     * <br></br>
     * Validation errors:
     *
     *  * Genre doesn't exist in data storage
     *
     * @param uuid UUID
     * @return genre
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable("uuid") uuid: String): Genre {
        return facade.get(uuid = uuid)
    }

    /**
     * Adds genre.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *
     * @param request request for changing genre
     * @return added genre
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeGenreRequest): Genre {
        return facade.add(request = request)
    }

    /**
     * Updates genre.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Genre doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing genre
     * @return updated genre
     */
    @PutMapping("{uuid}")
    fun update(
        @PathVariable("uuid") uuid: String,
        @RequestBody request: ChangeGenreRequest
    ): Genre {
        return facade.update(uuid = uuid, request = request)
    }

    /**
     * Removes genre.
     * <br></br>
     * Validation errors:
     *
     *  * Genre doesn't exist in data storage
     *
     * @param uuid UUID
     */
    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable("uuid") uuid: String) {
        facade.remove(uuid = uuid)
    }

    /**
     * Duplicates genre.
     * <br></br>
     * Validation errors:
     *
     *  * Genre doesn't exist in data storage
     *
     * @param uuid UUID
     * @return duplicated genre
     */
    @PostMapping("{uuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable("uuid") uuid: String): Genre {
        return facade.duplicate(uuid = uuid)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): GenreStatistics {
        return facade.getStatistics()
    }

}
