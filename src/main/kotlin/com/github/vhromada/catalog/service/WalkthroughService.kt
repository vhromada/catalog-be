package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Walkthrough
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents service for walkthroughs.
 *
 * @author Vladimir Hromada
 */
interface WalkthroughService {

    /**
     * Returns walkthrough.
     *
     * @param game game's ID
     * @return walkthrough
     * @throws InputException if walkthrough doesn't exist in data storage
     */
    fun getByGame(game: Int): Walkthrough

    /**
     * Returns walkthrough.
     *
     * @param uuid UUID
     * @return walkthrough
     * @throws InputException if walkthrough doesn't exist in data storage
     */
    fun getByUuid(uuid: String): Walkthrough

    /**
     * Stores walkthrough.
     *
     * @param walkthrough walkthrough
     * @return stored walkthrough
     */
    fun store(walkthrough: Walkthrough): Walkthrough

    /**
     * Removes walkthrough.
     *
     * @param walkthrough walkthrough
     */
    fun remove(walkthrough: Walkthrough)

}
