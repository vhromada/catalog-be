package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Cheat
import com.github.vhromada.catalog.entity.io.ChangeCheatRequest
import com.github.vhromada.catalog.facade.CheatFacade
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
 * A class represents controller for cheats.
 *
 * @author Vladimir Hromada
 */
@RestController("cheatController")
@RequestMapping("rest/games/{gameUuid}/cheats")
@Tag(name = "Games")
class CheatController(

    /**
     * Facade for cheats
     */
    private val facade: CheatFacade

) {

    /**
     * Returns cheat for game's UUID.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *
     * @param gameUuid game's UUID
     * @return cheat for game's UUID
     */
    @GetMapping
    fun find(@PathVariable("gameUuid") gameUuid: String): Cheat {
        return facade.find(game = gameUuid)
    }

    /**
     * Returns cheat.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *  * Cheat doesn't exist in data storage
     *
     * @param gameUuid game's UUID
     * @param cheatUuid cheat's UUID
     * @return cheat
     */
    @GetMapping("{cheatUuid}")
    fun get(
        @PathVariable("gameUuid") gameUuid: String,
        @PathVariable("cheatUuid") cheatUuid: String
    ): Cheat {
        return facade.get(game = gameUuid, uuid = cheatUuid)
    }

    /**
     * Adds cheat.
     * <br></br>
     * Validation errors:
     *
     *  * Cheat's data are null
     *  * Cheat's data contain null value
     *  * Action is null
     *  * Action is empty string
     *  * Description is null
     *  * Description is empty string
     *  * Game has already cheat in data storage
     *  * Game doesn't exist in data storage
     *
     * @param gameUuid game's UUID
     * @param request  request for changing game
     * @return add cheat
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Suppress("GrazieInspection")
    fun add(
        @PathVariable("gameUuid") gameUuid: String,
        @RequestBody request: ChangeCheatRequest
    ): Cheat {
        return facade.add(game = gameUuid, request = request)
    }

    /**
     * Updates cheat.
     * <br></br>
     * Validation errors:
     *
     *  * Cheat's data are null
     *  * Cheat's data contain null value
     *  * Action is null
     *  * Action is empty string
     *  * Description is null
     *  * Description is empty string
     *  * Game doesn't exist in data storage
     *  * Cheat doesn't exist in data storage
     *
     * @param gameUuid game's UUID
     * @param gameUuid cheat's UUID
     * @param request  request for changing game
     * @return updated cheat
     */
    @PutMapping("{cheatUuid}")
    fun update(
        @PathVariable("gameUuid") gameUuid: String,
        @PathVariable("cheatUuid") cheatUuid: String,
        @RequestBody request: ChangeCheatRequest
    ): Cheat {
        return facade.update(game = gameUuid, uuid = cheatUuid, request = request)
    }

    /**
     * Removes cheat.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *  * Cheat doesn't exist in data storage
     *
     * @param gameUuid game's UUID
     * @param gameUuid cheat's UUID
     */
    @DeleteMapping("{cheatUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable("gameUuid") gameUuid: String,
        @PathVariable("cheatUuid") cheatUuid: String
    ) {
        facade.remove(game = gameUuid, uuid = cheatUuid)
    }

}
