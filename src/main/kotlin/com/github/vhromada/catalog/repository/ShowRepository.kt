package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Show
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

/**
 * An interface represents repository for shows.
 *
 * @author Vladimir Hromada
 */
interface ShowRepository : JpaRepository<Show, Int>, JpaSpecificationExecutor<Show> {

    /**
     * Finds show by UUID.
     *
     * @param uuid UUID
     * @return show
     */
    fun findByUuid(uuid: String): Optional<Show>

}
