package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Handbook
import com.github.vhromada.catalog.entity.io.ChangeHandbookRequest
import com.github.vhromada.catalog.facade.HandbookFacade
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
 * A class represents controller for handbooks.
 *
 * @author Vladimir Hromada
 */
@RestController("handbookController")
@RequestMapping("rest/games/{gameUuid}/handbooks")
@Tag(name = "Games")
class HandbookController(

    /**
     * Facade for handbooks
     */
    private val facade: HandbookFacade

) {

    /**
     * Returns handbook for game's UUID.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *
     * @param gameUuid game's UUID
     * @return handbook for game's UUID
     */
    @GetMapping
    fun find(@PathVariable gameUuid: String): Handbook {
        return facade.find(game = gameUuid)
    }

    /**
     * Returns handbook.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *  * Handbook doesn't exist in data storage
     *
     * @param gameUuid        game's UUID
     * @param handbookUuid handbook's UUID
     * @return handbook
     */
    @GetMapping("{handbookUuid}")
    fun get(
        @PathVariable gameUuid: String,
        @PathVariable handbookUuid: String
    ): Handbook {
        return facade.get(game = gameUuid, uuid = handbookUuid)
    }

    /**
     * Adds handbook.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Game has already handbook in data storage
     *  * Game doesn't exist in data storage
     *
     * @param gameUuid game's UUID
     * @param request  request for changing handbook
     * @return add handbook
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(
        @PathVariable gameUuid: String,
        @RequestBody request: ChangeHandbookRequest
    ): Handbook {
        return facade.add(game = gameUuid, request = request)
    }

    /**
     * Updates handbook.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Game doesn't exist in data storage
     *  * Handbook doesn't exist in data storage
     *
     * @param gameUuid        game's UUID
     * @param handbookUuid handbook's UUID
     * @param request         request for changing handbook
     * @return updated handbook
     */
    @PutMapping("{handbookUuid}")
    fun update(
        @PathVariable gameUuid: String,
        @PathVariable handbookUuid: String,
        @RequestBody request: ChangeHandbookRequest
    ): Handbook {
        return facade.update(game = gameUuid, uuid = handbookUuid, request = request)
    }

    /**
     * Removes handbook.
     * <br></br>
     * Validation errors:
     *
     *  * Game doesn't exist in data storage
     *  * Handbook doesn't exist in data storage
     *
     * @param gameUuid        game's UUID
     * @param handbookUuid handbook's UUID
     */
    @DeleteMapping("{handbookUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable gameUuid: String,
        @PathVariable handbookUuid: String
    ) {
        facade.remove(game = gameUuid, uuid = handbookUuid)
    }

}
