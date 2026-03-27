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
     * @param uuid UUID
     * @return movie
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable uuid: String): Movie {
        return facade.get(uuid = uuid)
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
     * @param uuid    UUID
     * @param request request for changing movie
     * @return updated movie
     */
    @PutMapping("{uuid}")
    fun update(
        @PathVariable uuid: String,
        @RequestBody request: ChangeMovieRequest
    ): Movie {
        return facade.update(uuid = uuid, request = request)
    }

    /**
     * Removes movie.
     * <br></br>
     * Validation errors:
     *
     *  * Movie doesn't exist in data storage
     *
     * @param uuid UUID
     */
    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable uuid: String) {
        facade.remove(uuid = uuid)
    }

    /**
     * Duplicates movie.
     * <br></br>
     * Validation errors:
     *
     *  * Movie doesn't exist in data storage
     *
     * @param uuid UUID
     * @return duplicated movie
     */
    @PostMapping("{uuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable uuid: String): Movie {
        return facade.duplicate(uuid = uuid)
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
