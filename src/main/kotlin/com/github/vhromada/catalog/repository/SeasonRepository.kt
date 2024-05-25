package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Season
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * An interface represents repository for seasons.
 *
 * @author Vladimir Hromada
 */
interface SeasonRepository : JpaRepository<Season, Int> {

    /**
     * Returns seasons by show's ID.
     *
     * @param id       show's ID
     * @param pageable paging information
     * @return seasons
     */
    fun findAllByShowId(id: Int, pageable: Pageable): Page<Season>

    /**
     * Finds season by UUID.
     *
     * @param uuid UUID
     * @return season
     */
    fun findByUuid(uuid: String): Optional<Season>

}
