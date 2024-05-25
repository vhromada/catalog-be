package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

/**
 * An interface represents repository for books.
 *
 * @author Vladimir Hromada
 */
interface BookRepository : JpaRepository<Book, Int>, JpaSpecificationExecutor<Book> {

    /**
     * Finds book by UUID.
     *
     * @param uuid UUID
     * @return book
     */
    fun findByUuid(uuid: String): Optional<Book>

}
