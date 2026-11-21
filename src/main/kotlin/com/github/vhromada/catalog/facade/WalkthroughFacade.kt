package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Walkthrough
import com.github.vhromada.catalog.entity.io.ChangeWalkthroughRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for walkthroughs.
 *
 * @author Vladimir Hromada
 */
interface WalkthroughFacade {

    /**
     * Returns walkthrough by game's UUID.
     *
     * @param game game's UUID
     * @return walkthrough by game's UUID
     * @throws InputException if game doesn't exist in data storage
     * or walkthrough doesn't exist in data storage
     */
    fun find(game: String): Walkthrough

    /**
     * Returns walkthrough.
     *
     * @param game game's UUID
     * @param uuid walkthrough's UUID
     * @return walkthrough
     * @throws InputException if game doesn't exist in data storage
     * or walkthrough doesn't exist in data storage
     */
    fun get(game: String, uuid: String): Walkthrough

    /**
     * Adds walkthrough.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Game has already walkthrough in data storage
     *
     * @param game    game's UUID
     * @param request request for changing walkthrough
     * @return created walkthrough
     * @throws InputException if game doesn't exist in data storage
     * or request for changing walkthrough isn't valid
     */
    fun add(game: String, request: ChangeWalkthroughRequest): Walkthrough

    /**
     * Updates walkthrough.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty string
     *  * Walkthrough doesn't exist in data storage
     *
     * @param game    game's UUID
     * @param uuid    walkthrough's UUID
     * @param request request for changing walkthrough
     * @return updated walkthrough
     * @throws InputException if game doesn't exist in data storage
     * or request for changing walkthrough isn't valid
     */
    fun update(game: String, uuid: String, request: ChangeWalkthroughRequest): Walkthrough

    /**
     * Removes walkthrough.
     *
     * @param game game's UUID
     * @param uuid walkthrough's UUID
     * @throws InputException if game doesn't exist in data storage
     * or walkthrough doesn't exist in data storage
     */
    fun remove(game: String, uuid: String)

}
