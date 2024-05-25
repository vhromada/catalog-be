package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.BookItem
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeBookItemRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for bookItems.
 *
 * @author Vladimir Hromada
 */
interface BookItemFacade {

    /**
     * Returns page of book items by season's UUID and filter.
     *
     * @param book   book's UUID
     * @param filter filter
     * @return page of book items by season's UUID and filter
     * @throws InputException if book doesn't exist in data storage
     */
    fun findAll(book: String, filter: PagingFilter): Page<BookItem>

    /**
     * Returns book item.
     *
     * @param book book's UUID
     * @param uuid book item's UUID
     * @return book item
     * @throws InputException if book doesn't exist in data storage
     * or book item doesn't exist in data storage
     */
    fun get(book: String, uuid: String): BookItem

    /**
     * Adds book item.
     * <br></br>
     * Validation errors:
     *
     *  * Languages are null
     *  * Languages contain null value
     *  * Format is null
     *  * Format doesn't exist in data storage
     *
     * @param book    book's UUID
     * @param request request for changing book item
     * @return created book item
     * @throws InputException if book doesn't exist in data storage
     * or request for changing book item isn't valid
     */
    fun add(book: String, request: ChangeBookItemRequest): BookItem

    /**
     * Updates book item.
     * <br></br>
     * Validation errors:
     *
     *  * Languages are null
     *  * Languages contain null value
     *  * Format is null
     *  * Format doesn't exist in data storage
     *  * Book item doesn't exist in data storage
     *
     * @param book    book's UUID
     * @param uuid    bookItem's UUID
     * @param request request for changing book item
     * @return updated book item
     * @throws InputException if book doesn't exist in data storage
     * or request for changing book item isn't valid
     */
    fun update(book: String, uuid: String, request: ChangeBookItemRequest): BookItem

    /**
     * Removes book item.
     *
     * @param book book's UUID
     * @param uuid book item's UUID
     * @throws InputException if book doesn't exist in data storage
     * or book item doesn't exist in data storage
     */
    fun remove(book: String, uuid: String)

    /**
     * Duplicates data.
     *
     * @param book book's UUID
     * @param uuid book item's UUID
     * @return created duplicated book item
     * @throws InputException if book doesn't exist in data storage
     * or book item doesn't exist in data storage
     */
    fun duplicate(book: String, uuid: String): BookItem

}
