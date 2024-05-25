package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Book
import com.github.vhromada.catalog.domain.filter.BookFilter
import com.github.vhromada.catalog.entity.BookStatistics
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.BookMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.BookItemRepository
import com.github.vhromada.catalog.repository.BookRepository
import com.github.vhromada.catalog.service.BookService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for books.
 *
 * @author Vladimir Hromada
 */
@Service("bookService")
class BookServiceImpl(

    /**
     * Repository for books
     */
    private val bookRepository: BookRepository,

    /**
     * Repository for book items
     */
    private val bookItemRepository: BookItemRepository,

    /**
     * Mapper for books
     */
    private val mapper: BookMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : BookService {

    override fun search(filter: BookFilter, pageable: Pageable): Page<Book> {
        if (filter.isEmpty()) {
            return bookRepository.findAll(pageable)
        }
        return bookRepository.findAll(filter.toSpecification(), pageable)
    }

    override fun get(uuid: String): Book {
        return bookRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "BOOK_NOT_EXIST", message = "Book doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(book: Book): Book {
        return bookRepository.save(book)
    }

    @Transactional
    override fun remove(book: Book) {
        bookRepository.delete(book)
    }

    @Transactional
    override fun duplicate(book: Book): Book {
        val copy = book.copy(id = null, uuid = uuidProvider.getUuid(), authors = book.authors.map { it }.toMutableList(), items = mutableListOf())
        copy.items.addAll(book.items.map {
            it.copy(id = null, uuid = uuidProvider.getUuid(), languages = it.languages.map { language -> language }.toMutableList(), book = copy)
        })
        return bookRepository.save(copy)
    }

    override fun getStatistics(): BookStatistics {
        val booksCount = bookRepository.count()
        val bookItemsCount = bookItemRepository.count()
        return mapper.mapStatistics(booksCount = booksCount, bookItemsCount = bookItemsCount)
    }

}
