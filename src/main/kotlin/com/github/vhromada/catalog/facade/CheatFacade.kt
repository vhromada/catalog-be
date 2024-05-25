package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Cheat
import com.github.vhromada.catalog.entity.io.ChangeCheatRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for cheats.
 *
 * @author Vladimir Hromada
 */
interface CheatFacade {

    /**
     * Returns cheat by game's UUID.
     *
     * @param game game's UUID
     * @return cheat by game's UUID
     * @throws InputException if game doesn't exist in data storage
     * or cheat doesn't exist in data storage
     */
    fun find(game: String): Cheat

    /**
     * Returns cheat.
     *
     * @param game game's UUID
     * @param uuid cheat's UUID
     * @return cheat
     * @throws InputException if game doesn't exist in data storage
     * or cheat doesn't exist in data storage
     */
    fun get(game: String, uuid: String): Cheat

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
     *
     * @param game    game's UUID
     * @param request request for changing cheat
     * @return created cheat
     * @throws InputException if game doesn't exist in data storage
     * or request for changing cheat isn't valid
     */
    @Suppress("GrazieInspection")
    fun add(game: String, request: ChangeCheatRequest): Cheat

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
     *  * Cheat doesn't exist in data storage
     *
     * @param game    game's UUID
     * @param uuid    cheat's UUID
     * @param request request for changing cheat
     * @return updated cheat
     * @throws InputException if game doesn't exist in data storage
     * or request for changing cheat isn't valid
     */
    fun update(game: String, uuid: String, request: ChangeCheatRequest): Cheat

    /**
     * Removes cheat.
     *
     * @param game game's UUID
     * @param uuid cheat's UUID
     * @throws InputException if game doesn't exist in data storage
     * or cheat doesn't exist in data storage
     */
    fun remove(game: String, uuid: String)

}
