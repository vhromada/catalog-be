package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

/**
 * An interface represents repository for authors.
 *
 * @author Vladimir Hromada
 */
interface AuthorRepository : JpaRepository<Author, Int>, JpaSpecificationExecutor<Author> {

    /**
     * Finds author by UUID.
     *
     * @param uuid UUID
     * @return author
     */
    fun findByUuid(uuid: String): Optional<Author>

}
