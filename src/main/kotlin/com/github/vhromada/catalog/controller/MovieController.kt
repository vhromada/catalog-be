package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Movie
import com.github.vhromada.catalog.entity.MovieStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeMovieRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.MovieFacade
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
 * A class represents controller for movies.
 *
 * @author Vladimir Hromada
 */
@RestController("movieController")
@RequestMapping("rest/movies")
@Tag(name = "Movies")
class MovieController(

    /**
     * Facade for movies
     */
    private val facade: MovieFacade

) {

    /**
     * Returns list of movies for filter.
     *
     * @param filter filter
     * @return list of movies for filter
     */
    @GetMapping
    fun search(filter: MultipleNameFilter): Page<Movie> {
        return facade.search(filter = filter)
    }

    /**
     * Returns movie.
     * <br></br>
     * Validation errors:
     *
     *  * Movie doesn't exist in data storage
     *
     * @param id ID
     * @return movie
     */
    @GetMapping("{id}")
    fun get(@PathVariable("id") id: String): Movie {
        return facade.get(uuid = id)
    }

    /**
     * Adds movie.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Year is null
     *  * Year isn't between 1930 and current year
     *  * Languages are null
     *  * Languages are empty
     *  * Languages contain null value
     *  * Subtitles are null
     *  * Subtitles contain null value
     *  * Media are null
     *  * Media contain null value
     *  * Medium is negative value
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *  * Picture doesn't exist in data storage
     *  * Genre doesn't exist in data storage
     *
     * @param request request for changing movie
     * @return added movie
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeMovieRequest): Movie {
        return facade.add(request = request)
    }

    /**
     * Updates movie.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Year is null
     *  * Year isn't between 1930 and current year
     *  * Languages are null
     *  * Languages are empty
     *  * Languages contain null value
     *  * Subtitles are null
     *  * Subtitles contain null value
     *  * Media are null
     *  * Media contain null value
     *  * Medium is negative value
     *  * IMDB code isn't between 1 and 999999999
     *  * Genres are null
     *  * Genres contain null value
     *  * Genre is empty string
     *  * Picture doesn't exist in data storage
     *  * Genre doesn't exist in data storage
     *  * Movie doesn't exist in data storage
     *
     * @param id      ID
     * @param request request for changing movie
     * @return updated movie
     */
    @PutMapping("{id}")
    fun update(
        @PathVariable("id") id: String,
        @RequestBody request: ChangeMovieRequest
    ): Movie {
        return facade.update(uuid = id, request = request)
    }

    /**
     * Removes movie.
     * <br></br>
     * Validation errors:
     *
     *  * Movie doesn't exist in data storage
     *
     * @param id ID
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable("id") id: String) {
        facade.remove(uuid = id)
    }

    /**
     * Duplicates movie.
     * <br></br>
     * Validation errors:
     *
     *  * Movie doesn't exist in data storage
     *
     * @param id ID
     * @return duplicated movie
     */
    @PostMapping("{id}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable("id") id: String): Movie {
        return facade.duplicate(uuid = id)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): MovieStatistics {
        return facade.getStatistics()
    }

}
