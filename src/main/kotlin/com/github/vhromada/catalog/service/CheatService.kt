package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Cheat
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents service for cheats.
 *
 * @author Vladimir Hromada
 */
interface CheatService {

    /**
     * Returns cheat.
     *
     * @param game game's ID
     * @return cheat
     * @throws InputException if cheat doesn't exist in data storage
     */
    fun getByGame(game: Int): Cheat

    /**
     * Returns cheat.
     *
     * @param uuid UUID
     * @return cheat
     * @throws InputException if cheat doesn't exist in data storage
     */
    fun getByUuid(uuid: String): Cheat

    /**
     * Stores cheat.
     *
     * @param cheat cheat
     * @return stored cheat
     */
    fun store(cheat: Cheat): Cheat

    /**
     * Removes cheat.
     *
     * @param cheat cheat
     */
    fun remove(cheat: Cheat)

}
