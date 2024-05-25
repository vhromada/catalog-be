package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.BookItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * An interface represents repository for book items.
 *
 * @author Vladimir Hromada
 */
interface BookItemRepository : JpaRepository<BookItem, Int> {

    /**
     * Returns book items by book's ID.
     *
     * @param id       book's ID
     * @param pageable paging information
     * @return bookItems
     */
    fun findAllByBookId(id: Int, pageable: Pageable): Page<BookItem>

    /**
     * Finds book item by UUID.
     *
     * @param uuid UUID
     * @return bookItem
     */
    fun findByUuid(uuid: String): Optional<BookItem>

}
