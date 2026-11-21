package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Walkthrough
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * An interface represents repository for walkthroughs.
 *
 * @author Vladimir Hromada
 */
interface WalkthroughRepository : JpaRepository<Walkthrough, Int> {

    /**
     * Returns walkthrough by game's ID.
     *
     * @param id game's ID
     * @return walkthrough
     */
    fun findByGameId(id: Int): Optional<Walkthrough>

    /**
     * Finds walkthrough by UUID.
     *
     * @param uuid UUID
     * @return walkthrough
     */
    fun findByUuid(uuid: String): Optional<Walkthrough>

}
