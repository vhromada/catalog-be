package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Movie
import com.github.vhromada.catalog.domain.io.MediaStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.util.Optional

/**
 * An interface represents repository for movies.
 *
 * @author Vladimir Hromada
 */
interface MovieRepository : JpaRepository<Movie, Int>, JpaSpecificationExecutor<Movie> {

    /**
     * Finds movie by UUID.
     *
     * @param uuid UUID
     * @return movie
     */
    fun findByUuid(uuid: String): Optional<Movie>

    /**
     * Returns statistics for media.
     *
     * @return statistics for media
     */
    @Query("SELECT new com.github.vhromada.catalog.domain.io.MediaStatistics(COUNT(m.id), SUM(m.length)) FROM Medium m")
    fun getMediaStatistics(): MediaStatistics

}
