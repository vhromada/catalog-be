package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Handbook
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * An interface represents repository for handbooks.
 *
 * @author Vladimir Hromada
 */
interface HandbookRepository : JpaRepository<Handbook, Int> {

    /**
     * Returns handbook by game's ID.
     *
     * @param id game's ID
     * @return handbook
     */
    fun findByGameId(id: Int): Optional<Handbook>

    /**
     * Finds handbook by UUID.
     *
     * @param uuid UUID
     * @return handbook
     */
    fun findByUuid(uuid: String): Optional<Handbook>

}
