package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.BookItem
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for book items.
 *
 * @author Vladimir Hromada
 */
interface BookItemService {

    /**
     * Returns page of book items by book's ID.
     *
     * @param book     book's ID
     * @param pageable paging information
     * @return page of book items by book's ID
     */
    fun search(book: Int, pageable: Pageable): Page<BookItem>

    /**
     * Returns book item.
     *
     * @param uuid UUID
     * @return book item
     * @throws InputException if book item doesn't exist in data storage
     */
    fun get(uuid: String): BookItem

    /**
     * Stores book item.
     *
     * @param bookItem book item
     * @return stored book item
     */
    fun store(bookItem: BookItem): BookItem

    /**
     * Removes book item.
     *
     * @param bookItem book item
     */
    fun remove(bookItem: BookItem)

    /**
     * Duplicates book item.
     *
     * @param bookItem book item
     * @return duplicated book item
     */
    fun duplicate(bookItem: BookItem): BookItem

}
