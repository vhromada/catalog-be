package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Cheat
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * An interface represents repository for cheats.
 *
 * @author Vladimir Hromada
 */
interface CheatRepository : JpaRepository<Cheat, Int> {

    /**
     * Returns cheat by game's ID.
     *
     * @param id game's ID
     * @return cheat
     */
    fun findByGameId(id: Int): Optional<Cheat>

    /**
     * Finds cheat by UUID.
     *
     * @param uuid UUID
     * @return cheat
     */
    fun findByUuid(uuid: String): Optional<Cheat>

}
