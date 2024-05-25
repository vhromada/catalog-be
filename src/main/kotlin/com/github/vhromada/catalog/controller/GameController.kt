package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Game
import com.github.vhromada.catalog.entity.GameStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGameRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.GameFacade
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
 * A class represents controller for games.
 *
 * @author Vladimir Hromada
 */
@RestController("gameController")
@RequestMapping("rest/games")
@Tag(name = "Games")
class GameController(

    /**
     * Facade for games
     */
    private val facade: GameFacade

) {

    /**
     * Returns page of games for filter.
     *
     * @param filter filter
     * @return list of games for filter
     */
    @GetMapping
    fun search(filter: NameFilter): Page<Game> {
        return facade.search(filter = filter)
    }

    /**
     * Returns game.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *
     * @param uuid UUID
     * @return game
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable("uuid") uuid: String): Game {
        return facade.get(uuid = uuid)
    }

    /**
     * Adds game.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *  * Format is null
     *  * Crack is null
     *  * Serial key is null
     *  * Patch is null
     *  * Trainer is null
     *  * Data for trainer are null
     *  * Editor is null
     *  * Saves are null
     *
     * @param request request for changing game
     * @return added game
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeGameRequest): Game {
        return facade.add(request = request)
    }

    /**
     * Updates game.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *  * Format is null
     *  * Crack is null
     *  * Serial key is null
     *  * Patch is null
     *  * Trainer is null
     *  * Data for trainer are null
     *  * Editor is null
     *  * Saves are null
     *  * Game doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing game
     * @return updated game
     */
    @PutMapping("{uuid}")
    fun update(
        @PathVariable("uuid") uuid: String,
        @RequestBody request: ChangeGameRequest
    ): Game {
        return facade.update(uuid = uuid, request = request)
    }

    /**
     * Removes game.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *
     * @param uuid UUID
     */
    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable("uuid") uuid: String) {
        facade.remove(uuid = uuid)
    }

    /**
     * Duplicates game.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *
     * @param uuid UUID
     * @return duplicated game
     */
    @PostMapping("{uuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable("uuid") uuid: String): Game {
        return facade.duplicate(uuid = uuid)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): GameStatistics {
        return facade.getStatistics()
    }

}
