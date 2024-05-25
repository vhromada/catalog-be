package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Game
import com.github.vhromada.catalog.domain.filter.GameFilter
import com.github.vhromada.catalog.entity.GameStatistics
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for games.
 *
 * @author Vladimir Hromada
 */
interface GameService {

    /**
     * Returns page of games by filter.
     *
     * @param filter   filter
     * @param pageable paging information
     * @return page of games by filter
     */
    fun search(filter: GameFilter, pageable: Pageable): Page<Game>

    /**
     * Returns game.
     *
     * @param uuid UUID
     * @return game
     * @throws InputException if game doesn't exist in data storage
     */
    fun get(uuid: String): Game

    /**
     * Stores game.
     *
     * @param game game
     * @return stored game
     */
    fun store(game: Game): Game

    /**
     * Removes game.
     *
     * @param game game
     */
    fun remove(game: Game)

    /**
     * Duplicates game.
     *
     * @param game game
     * @return duplicated game
     */
    fun duplicate(game: Game): Game

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): GameStatistics

}
