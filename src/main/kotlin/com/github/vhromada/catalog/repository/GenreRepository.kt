package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Genre
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

/**
 * An interface represents repository for genres.
 *
 * @author Vladimir Hromada
 */
interface GenreRepository : JpaRepository<Genre, Int>, JpaSpecificationExecutor<Genre> {

    /**
     * Finds genre by UUID.
     *
     * @param uuid UUID
     * @return genre
     */
    fun findByUuid(uuid: String): Optional<Genre>

}
