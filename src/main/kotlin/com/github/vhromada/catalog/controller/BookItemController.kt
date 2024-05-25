package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.BookItem
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeBookItemRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.BookItemFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * A class represents controller for book items.
 *
 * @author Vladimir Hromada
 */
@RestController("bookItemController")
@RequestMapping("rest/books/{bookUuid}/items")
@Tag(name = "Books")
class BookItemController(

    /**
     * Facade for book items
     */
    private val facade: BookItemFacade

) {

    /**
     * Returns page of book items for specified books and filter.
     * <br></br>
     * Validation errors:
     *
     *  * Book doesn't exist in data storage
     *
     * @param bookUuid book's UUID
     * @param filter   filter
     * @return page of book items for specified books and filter
     */
    @GetMapping
    fun search(
        @PathVariable("bookUuid") bookUuid: String,
        filter: PagingFilter
    ): Page<BookItem> {
        return facade.findAll(book = bookUuid, filter = filter)
    }

    /**
     * Returns book item.
     * <br></br>
     * Validation errors:
     *
     *  * Book doesn't exist in data storage
     *  * Book item doesn't exist in data storage
     *
     * @param bookUuid     book's UUID
     * @param bookItemUuid book item's UUID
     * @return book item
     */
    @GetMapping("{bookItemUuid}")
    fun get(
        @PathVariable("bookUuid") bookUuid: String,
        @PathVariable("bookItemUuid") bookItemUuid: String
    ): BookItem {
        return facade.get(book = bookUuid, uuid = bookItemUuid)
    }

    /**
     * Adds book item.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Length of bookItem is null
     *  * Length of bookItem is negative value
     *  * Book doesn't exist in data storage
     *
     * @param bookUuid book's UUID
     * @param request   request fot changing book item
     * @return added bookItem
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(
        @PathVariable("bookUuid") bookUuid: String,
        @RequestBody request: ChangeBookItemRequest
    ): BookItem {
        return facade.add(book = bookUuid, request = request)
    }

    /**
     * Updates book item.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Length of bookItem is null
     *  * Length of bookItem is negative value
     *  * Book doesn't exist in data storage
     *  * Book item doesn't exist in data storage
     *
     * @param bookUuid     book's UUID
     * @param bookItemUuid book item's UUID
     * @param request      request fot changing book item
     * @return updated book item
     */
    @PutMapping("{bookItemUuid}")
    fun update(
        @PathVariable("bookUuid") bookUuid: String,
        @PathVariable("bookItemUuid") bookItemUuid: String,
        @RequestBody request: ChangeBookItemRequest
    ): BookItem {
        return facade.update(book = bookUuid, uuid = bookItemUuid, request = request)
    }

    /**
     * Removes book item.
     * <br></br>
     * Validation errors:
     *
     *  * Book doesn't exist in data storage
     *  * Book item doesn't exist in data storage
     *
     * @param bookUuid     book's UUID
     * @param bookItemUuid book item's UUID
     */
    @DeleteMapping("{bookItemUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable("bookUuid") bookUuid: String,
        @PathVariable("bookItemUuid") bookItemUuid: String
    ) {
        facade.remove(book = bookUuid, uuid = bookItemUuid)
    }

    /**
     * Duplicates book item.
     * <br></br>
     * Validation errors:
     *
     *  * Book doesn't exist in data storage
     *  * Book item doesn't exist in data storage
     *
     * @param bookUuid     book's UUID
     * @param bookItemUuid book item's UUID
     * @return duplicated book item
     */
    @PostMapping("{bookItemUuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(
        @PathVariable("bookUuid") bookUuid: String,
        @PathVariable("bookItemUuid") bookItemUuid: String
    ): BookItem {
        return facade.duplicate(book = bookUuid, uuid = bookItemUuid)
    }

}
