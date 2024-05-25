package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Game
import com.github.vhromada.catalog.entity.GameStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGameRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for games.
 *
 * @author Vladimir Hromada
 */
interface GameFacade {

    /**
     * Returns page of games for filter.
     *
     * @param filter filter
     * @return page of games for filter
     */
    fun search(filter: NameFilter): Page<Game>

    /**
     * Returns game.
     *
     * @param uuid UUID
     * @return game
     * @throws InputException if game doesn't exist in data storage
     */
    fun get(uuid: String): Game

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
     *  * Format doesn't exist in data storage
     *
     * @param request request for changing game
     * @return created game
     * @throws InputException if request for changing game isn't valid
     */
    fun add(request: ChangeGameRequest): Game

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
     *  * Format doesn't exist in data storage
     *  * Game doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing game
     * @return updated game
     * @throws InputException if request for changing game isn't valid
     */
    fun update(uuid: String, request: ChangeGameRequest): Game

    /**
     * Removes game.
     *
     * @param uuid UUID
     * @throws InputException if game doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Duplicates data.
     *
     * @param uuid UUID
     * @return created duplicated game
     * @throws InputException if game doesn't exist in data storage
     */
    fun duplicate(uuid: String): Game

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): GameStatistics

}
