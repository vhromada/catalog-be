package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Show
import com.github.vhromada.catalog.entity.ShowStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeShowRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.ShowFacade
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
 * A class represents controller for shows.
 *
 * @author Vladimir Hromada
 */
@RestController("showController")
@RequestMapping("rest/shows")
@Tag(name = "Shows")
class ShowController(

    /**
     * Facade for shows
     */
    private val facade: ShowFacade

) {

    /**
     * Returns list of shows for filter.
     *
     * @param filter filter
     * @return list of shows for filter
     */
    @GetMapping
    fun search(filter: MultipleNameFilter): Page<Show> {
        return facade.search(filter = filter)
    }

    /**
     * Returns show.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *
     * @param id ID
     * @return show
     */
    @GetMapping("{id}")
    fun get(@PathVariable("id") id: String): Show {
        return facade.get(uuid = id)
    }

    /**
     * Adds show.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *  * Picture doesn't exist in data storage
     *  * Genre doesn't exist in data storage
     *
     * @param request request for changing show
     * @return added show
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeShowRequest): Show {
        return facade.add(request = request)
    }

    /**
     * Updates show.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *  * Picture doesn't exist in data storage
     *  * Genre doesn't exist in data storage
     *  * Show doesn't exist in data storage
     *
     * @param id      ID
     * @param request request for changing show
     * @return updated show
     */
    @PutMapping("{id}")
    fun update(
        @PathVariable("id") id: String,
        @RequestBody request: ChangeShowRequest
    ): Show {
        return facade.update(uuid = id, request = request)
    }

    /**
     * Removes show.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *
     * @param id ID
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable("id") id: String) {
        facade.remove(uuid = id)
    }

    /**
     * Duplicates show.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *
     * @param id ID
     * @return duplicated show
     */
    @PostMapping("{id}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable("id") id: String): Show {
        return facade.duplicate(uuid = id)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): ShowStatistics {
        return facade.getStatistics()
    }

}
