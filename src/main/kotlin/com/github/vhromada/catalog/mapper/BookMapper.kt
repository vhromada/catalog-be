package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Book
import com.github.vhromada.catalog.domain.filter.BookFilter
import com.github.vhromada.catalog.entity.BookStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeBookRequest

/**
 * An interface represents mapper for books.
 *
 * @author Vladimir Hromada
 */
interface BookMapper {

    /**
     * Maps book.
     *
     * @param source book
     * @return mapped book
     */
    fun mapBook(source: Book): com.github.vhromada.catalog.entity.Book

    /**
     * Maps list of books.
     *
     * @param source list of books
     * @return mapped list of books
     */
    fun mapBooks(source: List<Book>): List<com.github.vhromada.catalog.entity.Book>

    /**
     * Maps request for changing book.
     *
     * @param source request for changing book
     * @return mapped book
     */
    fun mapRequest(source: ChangeBookRequest): Book

    /**
     * Maps filter for books.
     *
     * @param source filter for multiple names
     * @return mapped filter for books
     */
    fun mapFilter(source: MultipleNameFilter): BookFilter

    /**
     * Maps statistics.
     *
     * @param booksCount     count of books
     * @param bookItemsCount count of book items
     * @return statistics for books
     */
    fun mapStatistics(booksCount: Long, bookItemsCount: Long): BookStatistics

}
