package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Book
import com.github.vhromada.catalog.domain.filter.BookFilter
import com.github.vhromada.catalog.entity.BookStatistics
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for books.
 *
 * @author Vladimir Hromada
 */
interface BookService {

    /**
     * Returns page of books by filter.
     *
     * @param filter   filter
     * @param pageable paging information
     * @return page of books by filter
     */
    fun search(filter: BookFilter, pageable: Pageable): Page<Book>

    /**
     * Returns book.
     *
     * @param uuid UUID
     * @return book
     * @throws InputException if book doesn't exist in data storage
     */
    fun get(uuid: String): Book

    /**
     * Stores book.
     *
     * @param book book
     * @return stored book
     */
    fun store(book: Book): Book

    /**
     * Removes book.
     *
     * @param book book
     */
    fun remove(book: Book)

    /**
     * Duplicates book.
     *
     * @param book book
     * @return duplicated book
     */
    fun duplicate(book: Book): Book

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): BookStatistics

}
