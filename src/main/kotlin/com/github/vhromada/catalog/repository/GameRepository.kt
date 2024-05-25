package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Game
import com.github.vhromada.catalog.domain.io.GameStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.util.Optional

/**
 * An interface represents repository for games.
 *
 * @author Vladimir Hromada
 */
interface GameRepository : JpaRepository<Game, Int>, JpaSpecificationExecutor<Game> {

    /**
     * Finds game by UUID.
     *
     * @param uuid UUID
     * @return game
     */
    fun findByUuid(uuid: String): Optional<Game>

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @Query("SELECT new com.github.vhromada.catalog.domain.io.GameStatistics(COUNT(g.id), SUM(g.mediaCount)) FROM Game g")
    fun getStatistics(): GameStatistics

}
