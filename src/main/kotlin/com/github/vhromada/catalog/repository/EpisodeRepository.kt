package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Episode
import com.github.vhromada.catalog.domain.io.EpisodeStatistics
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

/**
 * An interface represents repository for episodes.
 *
 * @author Vladimir Hromada
 */
interface EpisodeRepository : JpaRepository<Episode, Int> {

    /**
     * Returns episodes by season's ID.
     *
     * @param id       season's ID
     * @param pageable paging information
     * @return episodes
     */
    fun findAllBySeasonId(id: Int, pageable: Pageable): Page<Episode>

    /**
     * Finds episode by UUID.
     *
     * @param uuid UUID
     * @return episode
     */
    fun findByUuid(uuid: String): Optional<Episode>

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @Query("SELECT new com.github.vhromada.catalog.domain.io.EpisodeStatistics(COUNT(e.id), SUM(e.length)) FROM Episode e")
    fun getStatistics(): EpisodeStatistics

}
