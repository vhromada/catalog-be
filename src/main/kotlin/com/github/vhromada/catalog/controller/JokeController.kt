package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Joke
import com.github.vhromada.catalog.entity.JokeStatistics
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeJokeRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.JokeFacade
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
 * A class represents controller for jokes.
 *
 * @author Vladimir Hromada
 */
@RestController("jokeController")
@RequestMapping("rest/jokes")
@Tag(name = "Jokes")
class JokeController(

    /**
     * Facade for jokes
     */
    private val facade: JokeFacade

) {

    /**
     * Returns page of jokes for filter.
     *
     * @param filter filter
     * @return page of jokes for filter
     */
    @GetMapping
    fun search(filter: PagingFilter): Page<Joke> {
        return facade.search(filter = filter)
    }

    /**
     * Returns joke.
     * <br></br>
     * Validation errors:
     *
     *  * Joke doesn't exist in data storage
     *
     * @param uuid UUID
     * @return joke
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable("uuid") uuid: String): Joke {
        return facade.get(uuid = uuid)
    }

    /**
     * Adds joke.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *
     * @param request request for changing joke
     * @return added joke
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeJokeRequest): Joke {
        return facade.add(request = request)
    }

    /**
     * Updates joke.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Joke doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing joke
     * @return updated joke
     */
    @PutMapping("{uuid}")
    fun update(
        @PathVariable("uuid") uuid: String,
        @RequestBody request: ChangeJokeRequest
    ): Joke {
        return facade.update(uuid = uuid, request = request)
    }

    /**
     * Removes joke.
     * <br></br>
     * Validation errors:
     *
     *  * Joke doesn't exist in data storage
     *
     * @param uuid UUID
     */
    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable("uuid") uuid: String) {
        facade.remove(uuid = uuid)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): JokeStatistics {
        return facade.getStatistics()
    }

}
