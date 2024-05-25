package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Book
import com.github.vhromada.catalog.entity.BookStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeBookRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for books.
 *
 * @author Vladimir Hromada
 */
interface BookFacade {

    /**
     * Returns page of books for filter.
     *
     * @param filter filter
     * @return page of books for filter
     */
    fun search(filter: MultipleNameFilter): Page<Book>

    /**
     * Returns book.
     *
     * @param uuid UUID
     * @return book
     * @throws InputException if book doesn't exist in data storage
     */
    fun get(uuid: String): Book

    /**
     * Adds book.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Description is null
     *  * Description is empty string
     *  * Authors are null
     *  * Authors contain null value
     *  * Authors is empty string
     *  * Author doesn't exist in data storage
     *
     * @param request request for changing book
     * @return created book
     * @throws InputException if request for changing book isn't valid
     */
    fun add(request: ChangeBookRequest): Book

    /**
     * Updates book.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Description is null
     *  * Description is empty string
     *  * Authors are null
     *  * Authors contain null value
     *  * Authors is empty string
     *  * Author doesn't exist in data storage
     *  * Book doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing book
     * @return updated book
     * @throws InputException if request for changing book isn't valid
     */
    fun update(uuid: String, request: ChangeBookRequest): Book

    /**
     * Removes book.
     *
     * @param uuid UUID
     * @throws InputException if book doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Duplicates data.
     *
     * @param uuid UUID
     * @return created duplicated book
     * @throws InputException if book doesn't exist in data storage
     */
    fun duplicate(uuid: String): Book

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): BookStatistics

}
