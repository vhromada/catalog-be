package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Handbook
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents service for handbooks.
 *
 * @author Vladimir Hromada
 */
interface HandbookService {

    /**
     * Returns handbook.
     *
     * @param game game's ID
     * @return handbook
     * @throws InputException if handbook doesn't exist in data storage
     */
    fun getByGame(game: Int): Handbook

    /**
     * Returns handbook.
     *
     * @param uuid UUID
     * @return handbook
     * @throws InputException if handbook doesn't exist in data storage
     */
    fun getByUuid(uuid: String): Handbook

    /**
     * Stores handbook.
     *
     * @param handbook handbook
     * @return stored handbook
     */
    fun store(handbook: Handbook): Handbook

    /**
     * Removes handbook.
     *
     * @param handbook handbook
     */
    fun remove(handbook: Handbook)

}
