package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Book
import com.github.vhromada.catalog.entity.BookStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeBookRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.BookFacade
import com.github.vhromada.catalog.mapper.BookMapper
import com.github.vhromada.catalog.service.AuthorService
import com.github.vhromada.catalog.service.BookService
import com.github.vhromada.catalog.validator.BookValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for books.
 *
 * @author Vladimir Hromada
 */
@Component("bookFacade")
class BookFacadeImpl(

    /**
     * Service for books
     */
    private val bookService: BookService,

    /**
     * Service for authors
     */
    private val authorService: AuthorService,

    /**
     * Mapper for books
     */
    private val mapper: BookMapper,

    /**
     * Validator for books
     */
    private val validator: BookValidator

) : BookFacade {

    override fun search(filter: MultipleNameFilter): Page<Book> {
        val books = bookService.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("normalizedCzechName", "id")))
        return Page(data = mapper.mapBooks(source = books.content), page = books)
    }

    override fun get(uuid: String): Book {
        return mapper.mapBook(source = bookService.get(uuid = uuid))
    }

    override fun add(request: ChangeBookRequest): Book {
        validator.validateRequest(request = request)
        val book = mapper.mapRequest(source = request)
        book.authors.addAll(getAuthors(request = request))
        return mapper.mapBook(source = bookService.store(book = book))
    }

    override fun update(uuid: String, request: ChangeBookRequest): Book {
        validator.validateRequest(request = request)
        val book = bookService.get(uuid = uuid)
        book.merge(book = mapper.mapRequest(source = request))
        book.authors.clear()
        book.authors.addAll(getAuthors(request = request))
        return mapper.mapBook(source = bookService.store(book = book))
    }

    override fun remove(uuid: String) {
        bookService.remove(book = bookService.get(uuid = uuid))
    }

    override fun duplicate(uuid: String): Book {
        return mapper.mapBook(source = bookService.duplicate(book = bookService.get(uuid = uuid)))
    }

    override fun getStatistics(): BookStatistics {
        return bookService.getStatistics()
    }

    /**
     * Returns authors.
     *
     * @param request request for changing book
     * @returns authors
     * @throws InputException if author doesn't exist in data storage
     */
    private fun getAuthors(request: ChangeBookRequest): List<com.github.vhromada.catalog.domain.Author> {
        return request.authors!!.filterNotNull().map { authorService.get(uuid = it) }
    }

}
