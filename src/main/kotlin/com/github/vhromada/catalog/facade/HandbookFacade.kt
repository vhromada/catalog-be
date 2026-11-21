package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Handbook
import com.github.vhromada.catalog.entity.io.ChangeHandbookRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for handbooks.
 *
 * @author Vladimir Hromada
 */
interface HandbookFacade {

    /**
     * Returns handbook by game's UUID.
     *
     * @param game game's UUID
     * @return handbook by game's UUID
     * @throws InputException if game doesn't exist in data storage
     * or handbook doesn't exist in data storage
     */
    fun find(game: String): Handbook

    /**
     * Returns handbook.
     *
     * @param game game's UUID
     * @param uuid handbook's UUID
     * @return handbook
     * @throws InputException if game doesn't exist in data storage
     * or handbook doesn't exist in data storage
     */
    fun get(game: String, uuid: String): Handbook

    /**
     * Adds handbook.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Game has already handbook in data storage
     *
     * @param game    game's UUID
     * @param request request for changing handbook
     * @return created handbook
     * @throws InputException if game doesn't exist in data storage
     * or request for changing handbook isn't valid
     */
    fun add(game: String, request: ChangeHandbookRequest): Handbook

    /**
     * Updates handbook.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Handbook doesn't exist in data storage
     *
     * @param game    game's UUID
     * @param uuid    handbook's UUID
     * @param request request for changing handbook
     * @return updated handbook
     * @throws InputException if game doesn't exist in data storage
     * or request for changing handbook isn't valid
     */
    fun update(game: String, uuid: String, request: ChangeHandbookRequest): Handbook

    /**
     * Removes handbook.
     *
     * @param game game's UUID
     * @param uuid handbook's UUID
     * @throws InputException if game doesn't exist in data storage
     * or handbook doesn't exist in data storage
     */
    fun remove(game: String, uuid: String)

}
