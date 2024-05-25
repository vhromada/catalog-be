package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Music
import com.github.vhromada.catalog.domain.io.MusicStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.util.Optional

/**
 * An interface represents repository for music.
 *
 * @author Vladimir Hromada
 */
interface MusicRepository : JpaRepository<Music, Int>, JpaSpecificationExecutor<Music> {

    /**
     * Finds music by UUID.
     *
     * @param uuid UUID
     * @return music
     */
    fun findByUuid(uuid: String): Optional<Music>

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @Query("SELECT new com.github.vhromada.catalog.domain.io.MusicStatistics(COUNT(m.id), SUM(m.mediaCount)) FROM Music m")
    fun getStatistics(): MusicStatistics

}
