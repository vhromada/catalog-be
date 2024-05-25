package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.BookItem
import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeBookItemRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.BookItemFacade
import com.github.vhromada.catalog.mapper.BookItemMapper
import com.github.vhromada.catalog.service.BookItemService
import com.github.vhromada.catalog.service.BookService
import com.github.vhromada.catalog.service.RegisterService
import com.github.vhromada.catalog.validator.BookItemValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for book items.
 *
 * @author Vladimir Hromada
 */
@Component("bookItemFacade")
class BookItemFacadeImpl(

    /**
     * Service for book items
     */
    private val bookItemService: BookItemService,

    /**
     * Service for books
     */
    private val bookService: BookService,

    /**
     * Service for registers
     */
    private val registerService: RegisterService,

    /**
     * Mapper for book items
     */
    private val mapper: BookItemMapper,

    /**
     * Validator for book items
     */
    private val validator: BookItemValidator

) : BookItemFacade {

    override fun findAll(book: String, filter: PagingFilter): Page<BookItem> {
        val bookItems = bookItemService.search(book = bookService.get(uuid = book).id!!, pageable = filter.toPageable(sort = Sort.by("id")))
        return Page(data = mapper.mapBookItems(bookItems.content), page = bookItems)
    }

    override fun get(book: String, uuid: String): BookItem {
        bookService.get(uuid = book)
        return mapper.mapBookItem(source = bookItemService.get(uuid = uuid))
    }

    override fun add(book: String, request: ChangeBookItemRequest): BookItem {
        val domainBook = bookService.get(uuid = book)
        validator.validateRequest(request = request)
        registerService.checkValue(type = RegisterType.BOOK_ITEM_FORMATS, code = request.format!!)
        return mapper.mapBookItem(source = bookItemService.store(bookItem = mapper.mapRequest(source = request).copy(book = domainBook)))
    }

    override fun update(book: String, uuid: String, request: ChangeBookItemRequest): BookItem {
        bookService.get(uuid = book)
        validator.validateRequest(request = request)
        registerService.checkValue(type = RegisterType.BOOK_ITEM_FORMATS, code = request.format!!)
        val bookItem = bookItemService.get(uuid = uuid)
        bookItem.merge(bookItem = mapper.mapRequest(source = request))
        return mapper.mapBookItem(source = bookItemService.store(bookItem = bookItem))
    }

    override fun remove(book: String, uuid: String) {
        bookService.get(uuid = book)
        bookItemService.remove(bookItem = bookItemService.get(uuid = uuid))
    }

    override fun duplicate(book: String, uuid: String): BookItem {
        bookService.get(uuid = book)
        return mapper.mapBookItem(source = bookItemService.duplicate(bookItem = bookItemService.get(uuid = uuid)))
    }

}
