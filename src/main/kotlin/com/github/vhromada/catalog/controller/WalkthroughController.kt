package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Walkthrough
import com.github.vhromada.catalog.entity.io.ChangeWalkthroughRequest
import com.github.vhromada.catalog.facade.WalkthroughFacade
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
 * A class represents controller for walkthroughs.
 *
 * @author Vladimir Hromada
 */
@RestController("walkthroughController")
@RequestMapping("rest/games/{gameUuid}/walkthroughs")
@Tag(name = "Games")
class WalkthroughController(

    /**
     * Facade for walkthroughs
     */
    private val facade: WalkthroughFacade

) {

    /**
     * Returns walkthrough for game's UUID.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *
     * @param gameUuid game's UUID
     * @return walkthrough for game's UUID
     */
    @GetMapping
    fun find(@PathVariable gameUuid: String): Walkthrough {
        return facade.find(game = gameUuid)
    }

    /**
     * Returns walkthrough.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *  * Walkthrough doesn't exist in data storage
     *
     * @param gameUuid        game's UUID
     * @param walkthroughUuid walkthrough's UUID
     * @return walkthrough
     */
    @GetMapping("{walkthroughUuid}")
    fun get(
        @PathVariable gameUuid: String,
        @PathVariable walkthroughUuid: String
    ): Walkthrough {
        return facade.get(game = gameUuid, uuid = walkthroughUuid)
    }

    /**
     * Adds walkthrough.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Game has already walkthrough in data storage
     *  * Game doesn't exist in data storage
     *
     * @param gameUuid game's UUID
     * @param request  request for changing walkthrough
     * @return add walkthrough
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(
        @PathVariable gameUuid: String,
        @RequestBody request: ChangeWalkthroughRequest
    ): Walkthrough {
        return facade.add(game = gameUuid, request = request)
    }

    /**
     * Updates walkthrough.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Game doesn't exist in data storage
     *  * Walkthrough doesn't exist in data storage
     *
     * @param gameUuid        game's UUID
     * @param walkthroughUuid walkthrough's UUID
     * @param request         request for changing walkthrough
     * @return updated walkthrough
     */
    @PutMapping("{walkthroughUuid}")
    fun update(
        @PathVariable gameUuid: String,
        @PathVariable walkthroughUuid: String,
        @RequestBody request: ChangeWalkthroughRequest
    ): Walkthrough {
        return facade.update(game = gameUuid, uuid = walkthroughUuid, request = request)
    }

    /**
     * Removes walkthrough.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *  * Walkthrough doesn't exist in data storage
     *
     * @param gameUuid        game's UUID
     * @param walkthroughUuid walkthrough's UUID
     */
    @DeleteMapping("{walkthroughUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable gameUuid: String,
        @PathVariable walkthroughUuid: String
    ) {
        facade.remove(game = gameUuid, uuid = walkthroughUuid)
    }

}
