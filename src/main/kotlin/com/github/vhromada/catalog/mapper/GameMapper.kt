package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Game
import com.github.vhromada.catalog.domain.filter.GameFilter
import com.github.vhromada.catalog.domain.io.GameStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGameRequest

/**
 * An interface represents mapper for games.
 *
 * @author Vladimir Hromada
 */
interface GameMapper {

    /**
     * Maps game.
     *
     * @param source game
     * @return mapped game
     */
    fun mapGame(source: Game): com.github.vhromada.catalog.entity.Game

    /**
     * Maps list of games.
     *
     * @param source list of games
     * @return mapped list of games
     */
    fun mapGames(source: List<Game>): List<com.github.vhromada.catalog.entity.Game>

    /**
     * Maps request for changing game.
     *
     * @param source request for changing game
     * @return mapped game
     */
    fun mapRequest(source: ChangeGameRequest): Game

    /**
     * Maps filter for games.
     *
     * @param source filter for name
     * @return mapped filter for games
     */
    fun mapFilter(source: NameFilter): GameFilter

    /**
     * Maps statistics.
     *
     * @param source statistics
     * @return statistics
     */
    fun mapStatistics(source: GameStatistics): com.github.vhromada.catalog.entity.GameStatistics

}
