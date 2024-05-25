package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.BookItem
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.BookItemRepository
import com.github.vhromada.catalog.repository.BookRepository
import com.github.vhromada.catalog.service.BookItemService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for book items.
 *
 * @author Vladimir Hromada
 */
@Service("bookItemService")
class BookItemServiceImpl(

    /**
     * Repository for book items
     */
    private val bookItemRepository: BookItemRepository,

    /**
     * Repository for books
     */
    private val bookRepository: BookRepository,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : BookItemService {

    override fun search(book: Int, pageable: Pageable): Page<BookItem> {
        return bookItemRepository.findAllByBookId(id = book, pageable = pageable)
    }

    override fun get(uuid: String): BookItem {
        return bookItemRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "BOOK_ITEM_NOT_EXIST", message = "Book item doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(bookItem: BookItem): BookItem {
        return bookItemRepository.save(bookItem)
    }

    @Transactional
    override fun remove(bookItem: BookItem) {
        val book = bookItem.book!!
        book.items.remove(bookItem)
        bookRepository.save(book)
    }

    @Transactional
    override fun duplicate(bookItem: BookItem): BookItem {
        val copy = bookItem.copy(id = null, uuid = uuidProvider.getUuid(), languages = bookItem.languages.map { it }.toMutableList())
        copy.book!!.items.add(copy)
        return bookItemRepository.save(copy)
    }

}
